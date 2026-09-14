package com.xb.modulith.order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单 REST 接口（order 模块）
 *
 * <p>注意：Controller 放在模块根包（公共区域），
 * 而 JPA 实体、仓储都藏在 internal/——这就是"模块对外只开放该开放的部分"。
 *
 * @author xb
 * @date 2026-09-14
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderManagement orderManagement;

    public OrderController(OrderManagement orderManagement) {
        this.orderManagement = orderManagement;
    }

    /**
     * 下单请求体
     */
    record CreateOrderRequest(String productCode, int quantity) {
    }

    /**
     * 创建订单
     *
     * @return 订单 ID
     */
    @PostMapping
    public Long place(@RequestBody CreateOrderRequest request) {
        return orderManagement.place(request.productCode(), request.quantity());
    }

    /**
     * 完成订单（触发 OrderCompleted 事件 → 库存扣减 + 发送通知）
     */
    @PostMapping("/{id}/complete")
    public void complete(@PathVariable Long id) {
        orderManagement.complete(id);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/{id}")
    public OrderDetails get(@PathVariable Long id) {
        return orderManagement.findById(id);
    }
}
