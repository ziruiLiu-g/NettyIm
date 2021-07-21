package com.lzr.client.client;

import com.lzr.client.ClientSender.ChatSender;
import com.lzr.client.ClientSender.LoginSender;
import com.lzr.client.clientCommand.*;
import com.lzr.client.feignClient.WebOperator;
import com.lzr.concurrent.FutureTaskScheduler;
import com.lzr.entity.ImNode;
import com.lzr.entity.LoginBack;
import com.lzr.im.common.bean.UserDTO;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * CommandController
 * 
 * Author: zirui liu
 * Date: 2021/7/21
 */
@Slf4j
@Data
@Service("CommandController")
public class CommandController {
    private int reConnectCount = 0;

    // chat command
    @Autowired
    ChatConsoleCommand chatConsoleCommand;

    // login command
    @Autowired
    LoginConsoleCommand loginConsoleCommand;

    // logout command
    @Autowired
    LogoutConsoleCommand logoutConsoleCommand;

    // menu
    @Autowired
    ClientCommandMenu clientCommandMenu;
    
    private Map<String, BaseCommand> commandMap;
    
    private String menuString;
    
    private ClientSession session;

    private boolean connectFlag = false;

    private UserDTO user;

    private Scanner scanner;

    private Channel channel;

    @Autowired
    private NettyClient nettyClient;

    @Autowired
    private ChatSender chatSender;

    @Autowired
    private LoginSender loginSender;
    
    GenericFutureListener<ChannelFuture> closeListener = (ChannelFuture f) -> {
        log.info(new Date() + ": conn close..");
        channel = f.channel();
        
        // create session
        ClientSession session = channel.attr(ClientSession.SESSION_KEY).get();
        session.close();
        
        notifyCommandThread();
    };
    
    GenericFutureListener<ChannelFuture> connectedListener = (ChannelFuture f) -> {
        final EventLoop eventLoop = f.channel().eventLoop();
        if (!f.isSuccess() && ++reConnectCount < 1) {
            log.info("conn failed! {} retry after 10s!",reConnectCount);
            eventLoop.schedule(() -> nettyClient.doConnect(), 10, TimeUnit.SECONDS);
            
            connectFlag = false;
        } else if (f.isSuccess()) {
            connectFlag = true;
            
            log.info("connect success");
            channel = f.channel();
            
            // create session
            session = new ClientSession(channel);
            session.setConnected(true);
            channel.closeFuture().addListener(closeListener);

            notifyCommandThread();
        } else {
            log.info("can not conn imserver!");
            connectFlag = false;
            notifyCommandThread();
        }
    };
    
    public void initCommandMap() {
        commandMap = new HashMap<>();
        commandMap.put(clientCommandMenu.getKey(), clientCommandMenu);
        commandMap.put(chatConsoleCommand.getKey(), chatConsoleCommand);
        commandMap.put(loginConsoleCommand.getKey(), loginConsoleCommand);
        commandMap.put(logoutConsoleCommand.getKey(), logoutConsoleCommand);

        Set<Map.Entry<String, BaseCommand>> entrys =
                commandMap.entrySet();
        Iterator<Map.Entry<String, BaseCommand>> iterator =
                entrys.iterator();
        
        StringBuilder menues = new StringBuilder();
        menues.append("[menu] ");
        while (iterator.hasNext()) {
            BaseCommand next = iterator.next().getValue();

            menues.append(next.getKey())
                    .append("->")
                    .append(next.getTip())
                    .append(" | ");
        }
        
        menuString = menues.toString();
        clientCommandMenu.setAllCommandsShow(menuString);
    }

    public void startConnectServer()
    {
        FutureTaskScheduler.add(() -> {
            nettyClient.setConnectedListener(connectedListener);
            nettyClient.doConnect();
        });
    }

    public synchronized void notifyCommandThread()
    {
        this.notify();
    }

    public synchronized void waitCommandThread()
    {
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * user connect to server
     */
    private void userLoginAndConnectToServer() {
        if (isConnectFlag()) {
            log.info("already login.");
            return;
        }
        LoginConsoleCommand command = (LoginConsoleCommand) commandMap.get(LoginConsoleCommand.KEY);
        command.exec(scanner);
        
        UserDTO user = new UserDTO();
        user.setUserId(command.getUserName());
        user.setToken(command.getPassword());
        user.setDevId("111");
        
        log.info("step1: login to gateway");
        LoginBack loginBack = WebOperator.login(command.getUserName(), command.getPassword());
        
        // get server node
        List<ImNode> nodeList = loginBack.getImNodeList();
        
        log.info("step2: conn to netty server");
        ImNode bestNode = null;
        if (nodeList.size() > 0) {
            Collections.sort(nodeList);
        } else {
            log.error("can not conn to netty server, because no nodes");
        }
        
        nettyClient.setConnectedListener(connectedListener);
        
        for (int i = 0; i < nodeList.size(); i++) {
            bestNode = nodeList.get(i);
            
            nettyClient.setHost(bestNode.getHost());
            nettyClient.setPort(bestNode.getPort());
            nettyClient.doConnect();
            waitCommandThread();
            
            if (connectFlag) {
                break;
            }
            
            if (i == nodeList.size()) {
                log.error("can not conn to all netty servers..");
                return;
            }
        }

        log.info("conn to netty server success");

        log.info("step3: login to netty server");
        this.user = user;
        session.setUser(user);
        loginSender.setUser(user);
        loginSender.setSession(session);
        loginSender.sendLoginMsg();
        waitCommandThread();
        
        connectFlag = true;
    }

    public void startCommandThread() throws InterruptedException {
        scanner = new Scanner(System.in);
        Thread.currentThread().setName("command thread");
        
        while (true) {
            while (connectFlag == false) {
                userLoginAndConnectToServer();
            }
            
            while (null != session) {
                ChatConsoleCommand command = (ChatConsoleCommand) commandMap.get(ChatConsoleCommand.KEY);
                command.exec(scanner);
                startOneChat(command);
            }
        }
    }

    private void startOneChat(ChatConsoleCommand c) {
        if (!isLogin())
        {
            log.info("please login");
            return;
        }
        chatSender.setSession(session);
        chatSender.setUser(user);
        chatSender.sendChatMsg(c.getToUserId(), c.getMessage());
    }

    public boolean isLogin() {
        if (null == session)
        {
            log.info("session is null");
            return false;
        }

        return session.isLogin();
    }
}
