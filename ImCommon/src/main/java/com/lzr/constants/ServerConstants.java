package com.lzr.constants;

import io.netty.util.AttributeKey;

/**
 * constants
 *
 * Author: zirui liu
 * Date: 2021/7/19
 */
public class ServerConstants {
    // parent path
    public static final String MANAGE_PATH = "/im/nodes";

    // working node prefix
    public static final String PATH_PREFIX = MANAGE_PATH + "/seq-";
    public static final String PATH_PREFIX_NO_STRIP =  "seq-";

    // znode for user counting
    public static final String COUNTER_PATH = "/im/OnlineCounter";

    public static final String WEB_URL = "http://localhost:8080";

    public static final AttributeKey<String> CHANNEL_NAME =
            AttributeKey.valueOf("CHANNEL_NAME");
}
