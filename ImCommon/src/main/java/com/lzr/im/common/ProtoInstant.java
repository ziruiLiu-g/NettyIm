package com.lzr.im.common;

/**
 * ProtoInstant
 *
 * Author: zirui liu
 * Date: 2021/7/19
 */
public class ProtoInstant {
    public static final short MAGIC_CODE = 0X86;

    public static final short VERSION_CODE = 0x01;

    public interface Platform {
        /**
         * windwos
         */
        public static final int WINDOWS = 1;

        /**
         * mac
         */
        public static final int MAC = 2;
        /**
         * android
         */
        public static final int ANDROID = 3;
        /**
         * IOS
         */
        public static final int IOS = 4;
        /**
         * WEB
         */
        public static final int WEB = 5;
        /**
         * 未知
         */
        public static final int UNKNOWN = 6;
    }

    /**
     * return code
     */
    public enum ResultCodeEnum {
        SUCCESS(0, "Success"),
        AUTH_FAILED(1, "login fail"),
        NO_TOKEN(2, "no token"),
        UNKNOW_ERROR(3, "unknown error"),
        ;

        private Integer code;
        private String desc;

        ResultCodeEnum(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {return code;};
        public String getDesc() {return desc;};
    }
}
