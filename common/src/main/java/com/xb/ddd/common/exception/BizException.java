package com.xb.ddd.common.exception;

/**
 * BizException - 业务异常的统一定义
 *
 * 携带错误码的业务异常，用于在分层架构中
 * 从领域层/应用层向接口层传递业务错误信息。
 * 接口层的全局异常处理器将其映射为 HTTP 响应。
 *
 * @author ibqy
 */
public class BizException extends RuntimeException {
    private final int code;

    /**
     * 构造业务异常
     * @param code 业务错误码
     * @param message 错误描述
     */
    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int code() { return code; }
}