package com.xb.ddd.domain.model.shared;

/**
 * 订单状态 —— 枚举值对象
 *
 * <p>作者：xb | 日期：2026-09-12</p>
 *
 * <p><b>DDD 知识点</b>：状态用枚举定义，状态转换规则写在 {@code canTransitTo} 方法中，
 * 避免状态散落在 if-else 中，降低理解成本。</p>
 */
public enum OrderStatus {
    CREATED,         // 已创建
    PAID,            // 已支付
    SHIPPED,         // 已发货
    DELIVERED,       // 已签收
    CANCELLED,       // 已取消
    REFUNDING,       // 退款中
    REFUNDED;        // 已退款

    /** 判断是否可转换到目标状态 */
    public boolean canTransitTo(OrderStatus target) {
        return switch (this) {
            case CREATED   -> target == PAID || target == CANCELLED;
            case PAID      -> target == SHIPPED || target == REFUNDING;
            case SHIPPED   -> target == DELIVERED;
            case DELIVERED -> false; // 终态
            case CANCELLED -> false;
            case REFUNDING -> target == REFUNDED || target == SHIPPED;
            case REFUNDED  -> false;
        };
    }
}