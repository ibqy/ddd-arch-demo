package com.xb.modulith.order.internal;

/**
 * OrderStatus - 订单状态枚举（order 模块内部）
 *
 * 定义订单的生命周期状态。状态转换规则
 * 由聚合根 Order.complete() 中的断言守护，
 * 保证状态流转的合法性。
 *
 * @author ibqy
 */
public enum OrderStatus {

    /** 已下单 */
    PLACED,

    /** 已完成 */
    COMPLETED
}
