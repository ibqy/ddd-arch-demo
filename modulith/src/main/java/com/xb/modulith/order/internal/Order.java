package com.xb.modulith.order.internal;

import com.xb.modulith.order.OrderCompleted;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.util.Assert;

/**
 * 订单聚合根（order 模块内部实现，对外隐藏）
 *
 * <p>继承 Spring Data 的 {@link AbstractAggregateRoot}：
 * 调用 registerEvent() 登记的领域事件，会在 repository.save() 时
 * 由 Spring Data 自动通过 ApplicationEventPublisher 发布——
 * 聚合根既是状态管理者，也是事件的事实来源。
 *
 * <p>DDD 知识点：
 * <ul>
 *     <li>聚合根负责守护不变量：只有「已下单」状态允许完成</li>
 *     <li>状态流转与事件登记在同一方法中原子发生，不会出现"状态变了事件没记"的裂缝</li>
 * </ul>
 *
 * <p>对比主示例（domain 模块）：那里的事件收集在普通 List 里，
 * 需要 popEvents() 手动取出并手动发布；这里的一切都由框架接管。
 *
 * @author xb
 * @date 2026-09-14
 */
@Entity
@Table(name = "orders")
public class Order extends AbstractAggregateRoot<Order> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_code")
    private String productCode;

    private int quantity;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    /**
     * 仅供 JPA 使用
     */
    protected Order() {
    }

    /**
     * 静态工厂：下单（保证初始状态合法）
     */
    public static Order place(String productCode, int quantity) {
        Assert.hasText(productCode, "商品编码不能为空");
        Assert.isTrue(quantity > 0, "数量必须大于 0");
        Order order = new Order();
        order.productCode = productCode;
        order.quantity = quantity;
        order.status = OrderStatus.PLACED;
        return order;
    }

    /**
     * 完成订单：状态流转 + 登记领域事件
     */
    public void complete() {
        // 1. 守护聚合不变量：只有「已下单」的订单才能完成
        Assert.isTrue(status == OrderStatus.PLACED, "只有已下单的订单才能完成，当前状态：" + status);
        // 2. 状态流转
        this.status = OrderStatus.COMPLETED;
        // 3. 登记事件：save() 时自动发布，无需自己维护事件列表
        registerEvent(new OrderCompleted(id, productCode, quantity));
    }

    public Long id() {
        return id;
    }

    public String productCode() {
        return productCode;
    }

    public int quantity() {
        return quantity;
    }

    public OrderStatus status() {
        return status;
    }
}
