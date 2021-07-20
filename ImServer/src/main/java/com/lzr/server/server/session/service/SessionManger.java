package com.lzr.server.server.session.service;

import com.lzr.entity.ImNode;
import com.lzr.im.common.bean.Notification;
import com.lzr.server.distributed.ImWorker;
import com.lzr.server.distributed.OnlineCounter;
import com.lzr.server.distributed.WorkerRouter;
import com.lzr.server.server.session.LocalSession;
import com.lzr.server.server.session.ServerSession;
import com.lzr.server.server.session.dao.SessionCacheDAO;
import com.lzr.server.server.session.dao.UserCacheDAO;
import com.lzr.server.server.session.entity.SessionCache;
import com.lzr.server.server.session.entity.UserCache;
import com.lzr.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SessionManger
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Slf4j
@Data
@Repository("SessionManger")
public class SessionManger {
    @Autowired
    UserCacheDAO userCacheDAO;

    @Autowired
    SessionCacheDAO sessionCacheDAO;

    private static SessionManger singleInstance = null;

    private ConcurrentHashMap<String, ServerSession> sessionMap = new ConcurrentHashMap();

    public static SessionManger instance()
    {
        return singleInstance;
    }

    public static void setSingleInstance(SessionManger singleInstance)
    {
        SessionManger.singleInstance = singleInstance;
    }

    /**
     * login successfully, add session
     */
    public void addLocalSession(LocalSession session) {
        // step1: store local session
        String sessionId = session.getSessionId();
        sessionMap.put(sessionId, session);
        
        String uid = session.getUser().getUserId();
        
        // step2: store session to redis
        ImNode node = ImWorker.getInstance().getLocalNodeInfo();
        SessionCache sessionCache = new SessionCache(sessionId, uid, node);
        sessionCacheDAO.save(sessionCache);
        
        // step3: add session to user
        userCacheDAO.addSession(uid, sessionCache);
        
        // step4: add online user
        OnlineCounter.getInstance().increment();
        log.info("local session added: {}, online total: {}",
                JsonUtil.pojoToJson(session.getUser()),
                OnlineCounter.getInstance().getCurValue());
        ImWorker.getInstance().incBalance();

        notifyOtherImNodeOnLine(session);
    }

    /**
     * get session by userid
     */
    public List<ServerSession> getSessionBy(String userId) {
        UserCache user = userCacheDAO.get(userId);
        
        if (null == user) {
            log.info("can not find info from redis, {}", userId);
            return null;
        }

        Map<String, SessionCache> allSession = user.getMap();
        if (null == allSession || allSession.size() == 0) {
            log.info("can not find info from redis, {}", userId);
            return null;
        }

        List<ServerSession> sessions = new LinkedList<>();
        allSession.values().stream().forEach(sessionCache -> {
            String sid = sessionCache.getSessionId();
            ServerSession session = sessionMap.get(sid);
            
            if (session == null) {
                session = new RemoteSession(sessionCache);
                sessionMap.put(sid, session);
            }
            sessions.add(session);
        });
        
        return sessions;
    }

    /**
     * close conn
     */
    public void closeSession(ChannelHandlerContext ctx) {
        LocalSession session = ctx.channel().attr(LocalSession.SESSION_KEY).get();
        
        if (null == session || !session.isValid()) {
            log.error("session is null or isValid");
            return;
        }
        
        session.close();
        // remove both local and remote
        this.removeSession(session.getSessionId());

        notifyOtherImNodeOffLine(session);
    }

    /**
     * notifyOtherImNodeOffLine
     *
     * @param session session
     */
    private void notifyOtherImNodeOffLine(LocalSession session)
    {

        if (null == session || session.isValid())
        {
            log.error("session is null or isValid");
            return;
        }


        int type = Notification.SESSION_OFF;

        Notification<Notification.ContentWrapper> notification = Notification.wrapContent(session.getSessionId());
        notification.setType(type);
        WorkerRouter.getInstance().sendNotification(JsonUtil.pojoToJson(notification));
    }

    /**
     * notifyOtherImNodeOnLine
     *
     * @param session session
     */
    private void notifyOtherImNodeOnLine(LocalSession session)
    {
        int type = Notification.SESSION_ON;
        Notification<Notification.ContentWrapper> notification = Notification.wrapContent(session.getSessionId());
        notification.setType(type);
        WorkerRouter.getInstance().sendNotification(JsonUtil.pojoToJson(notification));
    }

    /**
     * del session
     */
    public void removeSession(String sessionId)
    {
        if (!sessionMap.containsKey(sessionId)) return;
        ServerSession session = sessionMap.get(sessionId);
        String uid = session.getUserId();
        
        // decr user num
        OnlineCounter.getInstance().decrement();
        log.info("local session decr：{} offline,  online num:{} ", uid,
                OnlineCounter.getInstance().getCurValue());
        ImWorker.getInstance().decrBalance();
        
        // remove remote user cache
        userCacheDAO.removeSession(uid, sessionId);

        // remove session cache
        sessionCacheDAO.remove(sessionId);

        // remove local session
        sessionMap.remove(sessionId);
    }

    /**
     * remote user offline，del session
     */
    public void removeRemoteSession(String sessionId)
    {
        if (!sessionMap.containsKey(sessionId))
        {
            return;
        }
        sessionMap.remove(sessionId);
    }
}
