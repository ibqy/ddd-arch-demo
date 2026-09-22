package com.xb.ddd.domain.service;

import com.xb.ddd.domain.model.order.Order;
import com.xb.ddd.domain.model.shared.OrderStatus;

/**
 * 领域服务 —— 跨聚合的业务逻辑
 *
 * <p>作者：xb | 日期：2026-09-12</p>
 *
 * <p><b>DDD 知识点</b>：当一个业务操作涉及多个聚合根，或逻辑不适合放在实体内部时，
 * 提取到 DomainService 中。DomainService 无状态，只编排领域对象的操作。</p>
 *
 * <p><b>生产场景</b>：退款流程（需要同时操作 Order 聚合 + Payment 聚合）、
 * 下单锁库存（操作 Order + Inventory 两个聚合）。</p>
 *
 * @author ibqy
 */
public class OrderDomainService {

    /**
     * 处理退款请求
     * <p>判断当前订单是否允许退款，并执行状态转换。
     * 退款规则：PAID / SHIPPED → REFUNDING。</p>
     */
    public void requestRefund(Order order) {
        order.requestRefund();
    }

    /**
     * 完成退款
     * <p>REFUNDING → REFUNDED。</p>
     */
    public void completeRefund(Order order) {
        order.completeRefund();
    }
}