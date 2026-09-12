package com.xb.ddd.domain.model.order;

import com.xb.ddd.domain.event.OrderCreatedEvent;
import com.xb.ddd.domain.event.OrderEvent;
import com.xb.ddd.domain.model.shared.Address;
import com.xb.ddd.domain.model.shared.Money;
import com.xb.ddd.domain.model.shared.OrderStatus;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 订单 —— 聚合根（Aggregate Root）
 *
 * <p>作者：xb | 日期：2026-09-12</p>
 *
 * <p><b>DDD 知识点</b>：Order 是整个订单聚合的"根"，外部访问只能通过 Order：
 * <ul>
 *   <li>OrderItem 是聚合内的实体，不对外暴露仓储</li>
 *   <li>所有状态变更都通过 Order 的方法修改，保证聚合内一致性</li>
 *   <li>业务不变量（如金额合计）在领域方法中维护</li>
 * </ul></p>
 *
 * <p><b>生产场景</b>：电商订单、采购单、工单等有"生命周期+明细行"的业务对象。</p>
 */
public class Order {

    /** 订单 ID（聚合根的唯一标识） */
    private final Long id;
    /** 用户 ID */
    private final Long userId;
    /** 收货地址 */
    private Address address;
    /** 订单项列表 */
    private final List<OrderItem> items;
    /** 订单状态 */
    private OrderStatus status;
    /** 总金额（冗余字段，由领域方法维护一致性） */
    private Money totalAmount;
    /** 创建时间 */
    private final LocalDateTime createdAt;
    /** 未发布的领域事件 */
    private final List<OrderEvent> domainEvents = new ArrayList<>();

    public Order(Long id, Long userId, Address address, List<OrderItem> items) {
        this.id = id;
        this.userId = userId;
        this.address = address;
        this.items = Collections.unmodifiableList(items); // 外部无法修改列表
        this.status = OrderStatus.CREATED;
        this.totalAmount = items.stream()
            .map(OrderItem::subtotal)
            .reduce(Money.rmb(java.math.BigDecimal.ZERO), Money::add);
        this.createdAt = LocalDateTime.now();
        // 发布领域事件：订单已创建
        this.domainEvents.add(new OrderCreatedEvent(this.id, this.userId, this.totalAmount));
    }

    // ===== 业务方法 =====

    /** 支付 */
    public void pay() {
        assertStatus(OrderStatus.CREATED);
        this.status = OrderStatus.PAID;
    }

    /** 发货 */
    public void ship() {
        assertStatus(OrderStatus.PAID);
        this.status = OrderStatus.SHIPPED;
    }

    /** 签收 */
    public void deliver() {
        assertStatus(OrderStatus.SHIPPED);
        this.status = OrderStatus.DELIVERED;
    }

    /** 取消 */
    public void cancel() {
        assertStatus(OrderStatus.CREATED);
        this.status = OrderStatus.CANCELLED;
    }

    /** 修改收货地址（返回新地址保证不变性） */
    public void changeAddress(String newDetail) {
        assertStatus(OrderStatus.CREATED);
        this.address = this.address.withDetail(newDetail);
    }

    // ===== 内部方法 =====

    private void assertStatus(OrderStatus expected) {
        if (this.status != expected) {
            throw new IllegalStateException("当前状态 " + this.status + " 不能执行该操作，需要 " + expected);
        }
    }

    /** 获取并清空领域事件（由 infrastructure 层调用） */
    public List<OrderEvent> popEvents() {
        var events = List.copyOf(this.domainEvents);
        this.domainEvents.clear();
        return events;
    }

    // ===== getter =====

    public Long id() { return id; }
    public Long userId() { return userId; }
    public Address address() { return address; }
    public List<OrderItem> items() { return items; }
    public OrderStatus status() { return status; }
    public Money totalAmount() { return totalAmount; }
    public LocalDateTime createdAt() { return createdAt; }
}