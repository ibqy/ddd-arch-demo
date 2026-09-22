package com.xb.ddd;

import com.xb.ddd.domain.model.order.OrderItem;
import com.xb.ddd.domain.model.shared.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OrderItem 聚合内实体的单元测试
 *
 * 验证订单项的创建和小计金额计算逻辑。
 */
@DisplayName("OrderItem 实体测试")
class OrderItemTest {

    @Test
    @DisplayName("创建订单项")
    void createOrderItem() {
        OrderItem item = new OrderItem(1, 101L, "机械键盘", Money.rmb(new BigDecimal("399")), 2);
        assertEquals(1, item.seq());
        assertEquals(101L, item.productId());
        assertEquals("机械键盘", item.productName());
        assertEquals(2, item.quantity());
    }

    @Test
    @DisplayName("计算小计金额")
    void calculateSubtotal() {
        OrderItem item = new OrderItem(1, 101L, "机械键盘", Money.rmb(new BigDecimal("399")), 3);
        Money subtotal = item.subtotal();
        assertEquals(new BigDecimal("1197"), subtotal.amount());
    }

    @Test
    @DisplayName("数量为 1 时小计等于单价")
    void subtotalEqualsUnitPriceWhenQuantityIsOne() {
        Money unitPrice = Money.rmb(new BigDecimal("99.9"));
        OrderItem item = new OrderItem(1, 101L, "鼠标", unitPrice, 1);
        assertEquals(unitPrice, item.subtotal());
    }

    @Test
    @DisplayName("小数金额计算正确")
    void decimalCalculation() {
        OrderItem item = new OrderItem(1, 101L, "商品", Money.rmb(new BigDecimal("19.99")), 5);
        assertEquals(new BigDecimal("99.95"), item.subtotal().amount());
    }
}
