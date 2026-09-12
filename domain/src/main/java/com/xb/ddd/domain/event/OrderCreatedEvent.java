package com.xb.ddd.domain.event;

import com.xb.ddd.domain.model.shared.Money;

/**
 * 订单已创建事件
 *
 * <p>作者：xb | 日期：2026-09-12</p>
 */
public class OrderCreatedEvent extends OrderEvent {

    private final Long orderId;
    private final Long userId;
    private final Money totalAmount;

    public OrderCreatedEvent(Long orderId, Long userId, Money totalAmount) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
    }

    public Long orderId() { return orderId; }
    public Long userId() { return userId; }
    public Money totalAmount() { return totalAmount; }
}