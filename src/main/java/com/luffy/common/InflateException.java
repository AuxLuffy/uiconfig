package com.luffy.common;

/**
 * 解析布局异常
 *
 * @author sunzhangfei
 * @since 2021/8/20 12:56 下午
 */
public class InflateException extends RuntimeException {
    public InflateException(String cause) {
        super(cause);
    }

    public InflateException() {
    }

    public InflateException(String message, Throwable cause) {
        super(message, cause);
    }
}