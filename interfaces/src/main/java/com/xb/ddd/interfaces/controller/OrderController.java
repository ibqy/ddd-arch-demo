package com.xb.ddd.interfaces.controller;

import com.xb.ddd.application.dto.OrderCreateRequest;
import com.xb.ddd.application.dto.OrderResponse;
import com.xb.ddd.application.service.OrderAppService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 订单 REST 控制器 —— 接口层
 *
 * <p>作者：xb | 日期：2026-09-12</p>
 *
 * <p><b>DDD 知识点</b>：Interface 层处理 HTTP 协议细节（请求/响应格式、状态码），
 * 不包含业务逻辑。所有业务请求委托给 Application Service。</p>
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderAppService orderAppService;

    public OrderController(OrderAppService orderAppService) {
        this.orderAppService = orderAppService;
    }

    /** POST /api/orders — 创建订单 */
    @PostMapping
    public OrderResponse create(@Valid @RequestBody OrderCreateRequest request) {
        return orderAppService.createOrder(request);
    }

    /** GET /api/orders/{id} — 查询订单 */
    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable Long id) {
        return orderAppService.getOrder(id);
    }

    /** POST /api/orders/{id}/pay — 支付订单 */
    @PostMapping("/{id}/pay")
    public String pay(@PathVariable Long id) {
        orderAppService.payOrder(id);
        return "ok";
    }

    /** POST /api/orders/{id}/refund — 申请退款 */
    @PostMapping("/{id}/refund")
    public String requestRefund(@PathVariable Long id) {
        orderAppService.requestRefund(id);
        return "ok";
    }

    /** POST /api/orders/{id}/refund/complete — 完成退款 */
    @PostMapping("/{id}/refund/complete")
    public String completeRefund(@PathVariable Long id) {
        orderAppService.completeRefund(id);
        return "ok";
    }
}