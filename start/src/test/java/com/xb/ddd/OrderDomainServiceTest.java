package com.xb.ddd;

import com.xb.ddd.domain.model.order.Order;
import com.xb.ddd.domain.model.order.OrderItem;
import com.xb.ddd.domain.model.shared.Address;
import com.xb.ddd.domain.model.shared.Money;
import com.xb.ddd.domain.model.shared.OrderStatus;
import com.xb.ddd.domain.service.OrderDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderDomainService 领域服务测试")
class OrderDomainServiceTest {

    private OrderDomainService domainService;
    private Address address;
    private List<OrderItem> items;

    @BeforeEach
    void setUp() {
        domainService = new OrderDomainService();
        address = new Address("广东省", "深圳市", "南山区", "科技园");
        items = List.of(
            new OrderItem(1, 101L, "机械键盘", Money.rmb(new BigDecimal("399")), 1)
        );
    }

    @Test
    @DisplayName("PAID 状态可以退款")
    void requestRefund_whenPaid() {
        Order order = new Order(1L, 1001L, address, items);
        order.pay();
        assertDoesNotThrow(() -> domainService.requestRefund(order));
    }

    @Test
    @DisplayName("SHIPPED 状态可以退款")
    void requestRefund_whenShipped() {
        Order order = new Order(1L, 1001L, address, items);
        order.pay();
        order.ship();
        assertDoesNotThrow(() -> domainService.requestRefund(order));
    }

    @Test
    @DisplayName("CREATED 状态不能退款")
    void requestRefund_whenCreated_throws() {
        Order order = new Order(1L, 1001L, address, items);
        assertThrows(IllegalStateException.class, () -> domainService.requestRefund(order));
    }

    @Test
    @DisplayName("CANCELLED 状态不能退款")
    void requestRefund_whenCancelled_throws() {
        Order order = new Order(1L, 1001L, address, items);
        order.cancel();
        assertThrows(IllegalStateException.class, () -> domainService.requestRefund(order));
    }

    @Test
    @DisplayName("DELIVERED 状态不能退款")
    void requestRefund_whenDelivered_throws() {
        Order order = new Order(1L, 1001L, address, items);
        order.pay();
        order.ship();
        order.deliver();
        assertThrows(IllegalStateException.class, () -> domainService.requestRefund(order));
    }
}
