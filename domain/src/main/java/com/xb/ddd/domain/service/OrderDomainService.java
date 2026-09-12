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
 */
public class OrderDomainService {

    /**
     * 处理退款请求
     * <p>判断当前订单是否允许退款，并执行状态转换。</p>
     */
    public void requestRefund(Order order) {
        // 只有已支付且未取消的订单才能退款
        if (order.status() != OrderStatus.PAID && order.status() != OrderStatus.SHIPPED) {
            throw new IllegalStateException("当前状态不可退款：" + order.status());
        }
        // 这里仅演示状态判断，实际生产还需要验证退款金额、支付流水等
    }
}