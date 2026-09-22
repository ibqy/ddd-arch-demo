package com.xb.ddd.domain.event;

import com.xb.ddd.domain.model.shared.Money;

/**
 * OrderCreatedEvent - 订单已创建领域事件
 *
 * 当 Order 聚合根被创建时发布。
 * 下游模块可监听此事件执行积分发放、
 * 库存预占等异步操作，实现跨聚合解耦。
 *
 * @author ibqy
 */
public class OrderCreatedEvent extends OrderEvent {

    private final Long orderId;
    private final Long userId;
    private final Money totalAmount;

    /**
     * 构造订单创建事件
     * @param orderId 订单 ID
     * @param userId 下单用户 ID
     * @param totalAmount 订单总金额
     */
    public OrderCreatedEvent(Long orderId, Long userId, Money totalAmount) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
    }

    public Long orderId() { return orderId; }
    public Long userId() { return userId; }
    public Money totalAmount() { return totalAmount; }
}