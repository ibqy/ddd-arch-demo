package com.xb.ddd.domain.event;

import com.xb.ddd.domain.model.shared.Money;

/**
 * 订单已支付事件
 *
 * <p>作者：xb | 日期：2026-09-12</p>
 */
public class OrderPaidEvent extends OrderEvent {

    private final Long orderId;
    private final Money paidAmount;

    public OrderPaidEvent(Long orderId, Money paidAmount) {
        this.orderId = orderId;
        this.paidAmount = paidAmount;
    }

    public Long orderId() { return orderId; }
    public Money paidAmount() { return paidAmount; }
}