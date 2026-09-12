package com.xb.ddd.common.exception;

/**
 * 业务异常
 *
 * <p>作者：xb | 日期：2026-09-12</p>
 */
public class BizException extends RuntimeException {
    private final int code;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int code() { return code; }
}