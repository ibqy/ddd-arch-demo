package com.xb.ddd;

import com.xb.ddd.domain.model.order.Order;
import com.xb.ddd.domain.model.order.OrderItem;
import com.xb.ddd.domain.model.shared.Address;
import com.xb.ddd.domain.model.shared.Money;
import com.xb.ddd.infrastructure.repository.InMemoryOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InMemoryOrderRepository 仓储测试")
class InMemoryOrderRepositoryTest {

    private InMemoryOrderRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOrderRepository();
    }

    @Test
    @DisplayName("保存并查询订单")
    void saveAndFind() {
        Order order = createOrder(1L);
        repository.save(order);

        Optional<Order> found = repository.findById(1L);

        assertTrue(found.isPresent());
        assertEquals(1L, found.get().id());
    }

    @Test
    @DisplayName("查询不存在的订单返回空")
    void findById_notFound() {
        Optional<Order> found = repository.findById(999L);
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("保存多个订单")
    void saveMultiple() {
        repository.save(createOrder(1L));
        repository.save(createOrder(2L));
        repository.save(createOrder(3L));

        assertTrue(repository.findById(1L).isPresent());
        assertTrue(repository.findById(2L).isPresent());
        assertTrue(repository.findById(3L).isPresent());
    }

    @Test
    @DisplayName("重复保存同一 ID 覆盖")
    void save_overwrite() {
        Order order1 = createOrder(1L);
        repository.save(order1);

        Order order2 = createOrder(2L);
        repository.save(new Order(1L, 2002L, order1.address(), order1.items()));

        Optional<Order> found = repository.findById(1L);
        assertTrue(found.isPresent());
        assertEquals(2002L, found.get().userId());
    }

    private Order createOrder(Long id) {
        Address address = new Address("广东省", "深圳市", "南山区", "科技园");
        List<OrderItem> items = List.of(
            new OrderItem(1, 101L, "键盘", Money.rmb(new BigDecimal("100")), 1)
        );
        return new Order(id, 1001L, address, items);
    }
}
