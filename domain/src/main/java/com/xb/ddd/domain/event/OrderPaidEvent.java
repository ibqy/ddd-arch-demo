package com.xb.ddd.domain.event;

import com.xb.ddd.domain.model.shared.Money;

/**
 * OrderPaidEvent - 订单已支付领域事件
 *
 * 当 Order 聚合根执行 pay() 方法后发布。
 * 通知模块、发货模块可监听此事件，
 * 实现支付成功后的异步业务流转。
 *
 * @author ibqy
 */
public class OrderPaidEvent extends OrderEvent {

    private final Long orderId;
    private final Money paidAmount;

    /**
     * 构造订单支付事件
     * @param orderId 订单 ID
     * @param paidAmount 支付金额
     */
    public OrderPaidEvent(Long orderId, Money paidAmount) {
        this.orderId = orderId;
        this.paidAmount = paidAmount;
    }

    public Long orderId() { return orderId; }
    public Money paidAmount() { return paidAmount; }
}