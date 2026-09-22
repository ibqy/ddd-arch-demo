package com.xb.ddd.infrastructure.repository;

import com.xb.ddd.domain.model.order.Order;
import com.xb.ddd.domain.repository.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 订单仓储实现 —— 内存版本（教学用）
 *
 * <p>作者：xb | 日期：2026-09-12</p>
 *
 * <p><b>DDD 知识点</b>：Repository 实现在 infrastructure 层，
 * 但接口定义在 domain 层。这样换数据库时只需要换这个类。
 * 实际项目可替换为 MyBatis / JPA 实现。</p>
 *
 * <p><b>生产场景</b>：使用 MyBatis-Plus 或 Spring Data JPA 实现持久化，
 * 通过 ORM 映射 Order + OrderItem 到数据库表。</p>
 *
 * @author ibqy
 */
@Repository
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<Long, Order> store = new ConcurrentHashMap<>();

    @Override
    public void save(Order order) {
        store.put(order.id(), order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }
}