package com.xb.ddd.interfaces.controller;

import com.xb.ddd.common.exception.BizException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 *
 * <p>作者：xb | 日期：2026-09-12</p>
 *
 * <p><b>DDD 知识点</b>：Interface 层负责将领域异常映射为 HTTP 响应。
 * 业务异常（BizException）→ 400，非法状态操作 → 409，未知异常 → 500。
 * 领域层不关心 HTTP 状态码，这些协议细节属于接口层。</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ResponseEntity<Map<String, Object>> handleBizException(BizException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", e.code());
        body.put("message", e.getMessage());
        body.put("time", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", 409);
        body.put("message", e.getMessage());
        body.put("time", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
}
