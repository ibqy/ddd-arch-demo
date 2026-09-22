package com.xb.ddd;

import com.xb.ddd.domain.event.OrderCreatedEvent;
import com.xb.ddd.domain.event.OrderEvent;
import com.xb.ddd.domain.event.OrderPaidEvent;
import com.xb.ddd.domain.model.order.Order;
import com.xb.ddd.domain.model.order.OrderItem;
import com.xb.ddd.domain.model.shared.Address;
import com.xb.ddd.domain.model.shared.Money;
import com.xb.ddd.domain.model.shared.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Order 聚合根的核心单元测试
 *
 * 覆盖状态机流转、领域事件发布、金额计算、
 * 地址修改等聚合根的关键行为，是 DDD 教学的重点。
 */
@DisplayName("Order 聚合根测试")
class OrderTest {

    private Address address;
    private List<OrderItem> items;

    @BeforeEach
    void setUp() {
        address = new Address("广东省", "深圳市", "南山区", "科技园");
        items = List.of(
            new OrderItem(1, 101L, "机械键盘", Money.rmb(new BigDecimal("399")), 1),
            new OrderItem(2, 102L, "鼠标", Money.rmb(new BigDecimal("99")), 2)
        );
    }

    @Test
    @DisplayName("创建订单 - 初始状态为 CREATED")
    void createOrder_initialStatusCreated() {
        Order order = new Order(1L, 1001L, address, items);
        assertEquals(OrderStatus.CREATED, order.status());
        assertEquals(1L, order.id());
        assertEquals(1001L, order.userId());
    }

    @Test
    @DisplayName("创建订单 - 自动计算总金额")
    void createOrder_autoCalculateTotalAmount() {
        Order order = new Order(1L, 1001L, address, items);
        Money expected = Money.rmb(new BigDecimal("597"));
        assertEquals(expected, order.totalAmount());
    }

    @Test
    @DisplayName("创建订单 - 发布 OrderCreatedEvent")
    void createOrder_publishesDomainEvent() {
        Order order = new Order(1L, 1001L, address, items);
        List<OrderEvent> events = order.popEvents();
        assertEquals(1, events.size());
        assertInstanceOf(OrderCreatedEvent.class, events.get(0));
    }

    @Test
    @DisplayName("items 列表不可变")
    void itemsListImmutable() {
        Order order = new Order(1L, 1001L, address, items);
        assertThrows(UnsupportedOperationException.class,
            () -> order.items().add(new OrderItem(3, 103L, "耳机", Money.rmb(BigDecimal.TEN), 1)));
    }

    @Nested
    @DisplayName("状态机转换")
    class StateTransition {

        private Order order;

        @BeforeEach
        void setUp() {
            order = new Order(1L, 1001L, address, items);
        }

        @Test
        @DisplayName("CREATED → PAID")
        void pay() {
            order.pay();
            assertEquals(OrderStatus.PAID, order.status());
        }

        @Test
        @DisplayName("PAID → SHIPPED")
        void ship() {
            order.pay();
            order.ship();
            assertEquals(OrderStatus.SHIPPED, order.status());
        }

        @Test
        @DisplayName("SHIPPED → DELIVERED")
        void deliver() {
            order.pay();
            order.ship();
            order.deliver();
            assertEquals(OrderStatus.DELIVERED, order.status());
        }

        @Test
        @DisplayName("CREATED → CANCELLED")
        void cancel() {
            order.cancel();
            assertEquals(OrderStatus.CANCELLED, order.status());
        }

        @Test
        @DisplayName("PAID 状态不能直接取消")
        void cannotCancelWhenPaid() {
            order.pay();
            assertThrows(IllegalStateException.class, order::cancel);
        }

        @Test
        @DisplayName("CREATED 状态不能发货")
        void cannotShipWhenCreated() {
            assertThrows(IllegalStateException.class, order::ship);
        }

        @Test
        @DisplayName("DELIVERED 状态不能重复签收")
        void cannotDeliverTwice() {
            order.pay();
            order.ship();
            order.deliver();
            assertThrows(IllegalStateException.class, order::deliver);
        }

        @Test
        @DisplayName("CANCELLED 状态不能支付")
        void cannotPayWhenCancelled() {
            order.cancel();
            assertThrows(IllegalStateException.class, order::pay);
        }

        @Test
        @DisplayName("PAID → REFUNDING")
        void requestRefund() {
            order.pay();
            order.requestRefund();
            assertEquals(OrderStatus.REFUNDING, order.status());
        }

        @Test
        @DisplayName("REFUNDING → REFUNDED")
        void completeRefund() {
            order.pay();
            order.requestRefund();
            order.completeRefund();
            assertEquals(OrderStatus.REFUNDED, order.status());
        }

        @Test
        @DisplayName("CREATED 状态不能退款")
        void cannotRefundWhenCreated() {
            assertThrows(IllegalStateException.class, order::requestRefund);
        }
    }

    @Nested
    @DisplayName("修改地址")
    class ChangeAddress {

        @Test
        @DisplayName("CREATED 状态可以修改地址")
        void canChangeAddressWhenCreated() {
            Order order = new Order(1L, 1001L, address, items);
            order.changeAddress("科技园D座");
            assertEquals("科技园D座", order.address().detail());
        }

        @Test
        @DisplayName("PAID 状态不能修改地址")
        void cannotChangeAddressWhenPaid() {
            Order order = new Order(1L, 1001L, address, items);
            order.pay();
            assertThrows(IllegalStateException.class, () -> order.changeAddress("新地址"));
        }
    }

    @Nested
    @DisplayName("领域事件")
    class DomainEvents {

        @Test
        @DisplayName("popEvents 返回并清空事件")
        void popEvents_returnsAndClears() {
            Order order = new Order(1L, 1001L, address, items);
            List<OrderEvent> first = order.popEvents();
            assertEquals(1, first.size());

            List<OrderEvent> second = order.popEvents();
            assertTrue(second.isEmpty());
        }

        @Test
        @DisplayName("pay() 发布 OrderPaidEvent")
        void pay_publishesOrderPaidEvent() {
            Order order = new Order(1L, 1001L, address, items);
            order.popEvents();
            order.pay();
            List<OrderEvent> events = order.popEvents();
            assertEquals(1, events.size());
            assertInstanceOf(OrderPaidEvent.class, events.get(0));
            assertEquals(1L, ((OrderPaidEvent) events.get(0)).orderId());
        }
    }
}
