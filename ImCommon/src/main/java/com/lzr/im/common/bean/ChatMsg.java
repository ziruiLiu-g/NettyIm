package com.lzr.im.common.bean;

import com.lzr.im.common.bean.msg.ProtoMsg;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

@Data
public class ChatMsg
{
    public enum MSGTYPE
    {
        TEXT,
        AUDIO,
        VIDEO,
        POS,
        OTHER;
    }

    public ChatMsg(UserDTO user)
    {
        if (null == user)
        {
            return;
        }
        this.user = user;
        this.setTime(System.currentTimeMillis());
        this.setFrom(user.getUserId());
        this.setFromNick(user.getNickName());

    }

    private UserDTO user;

    private long msgId;
    private String from;
    private String to;
    private long time;
    private MSGTYPE msgType;
    private String content;
    private String url;          
    private String property;     
    private String fromNick;     
    private String json;    


    public void fillMsg(ProtoMsg.MessageRequest.Builder cb)
    {
        if (msgId > 0)
        {
            cb.setMsgId(msgId);
        }
        if (StringUtils.isNotEmpty(from))
        {
            cb.setFrom(from);
        }
        if (StringUtils.isNotEmpty(to))
        {
            cb.setTo(to);
        }
        if (time > 0)
        {
            cb.setTime(time);
        }
        if (msgType != null)
        {
            cb.setMsgType(msgType.ordinal());
        }
        if (StringUtils.isNotEmpty(content))
        {
            cb.setContent(content);
        }
        if (StringUtils.isNotEmpty(url))
        {
            cb.setUrl(url);
        }
        if (StringUtils.isNotEmpty(property))
        {
            cb.setProperty(property);
        }
        if (StringUtils.isNotEmpty(fromNick))
        {
            cb.setFromNick(fromNick);
        }

        if (StringUtils.isNotEmpty(json))
        {
            cb.setJson(json);
        }
    }

}
