package com.lzr.im.common.bean;

import lombok.Data;

/**
 * Notification
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Data
public class Notification<T> {
    public static final int SESSION_ON = 10;//上线的通知
    public static final int SESSION_OFF = 20;//下线的通知
    public static final int CONNECT_FINISHED = 30;//节点的链接成功
    private int type;
    private T data;

    public Notification() { }

    public Notification(T t)
    {
        data = t;
    }
    
    public static Notification<ContentWrapper> wrapContent(String content) {
        ContentWrapper wrapper = new ContentWrapper();
        wrapper.setContent(content);
        return new Notification<>(wrapper);
    }

    @Data
    public static class ContentWrapper
    {
        String content;
    }

    public String getWrapperContent()
    {
        if (data instanceof ContentWrapper)
        {
            return ((ContentWrapper) data).getContent();
        }
        return null;
    }
}
