package com.xb.ddd.application.service;

import com.xb.ddd.application.dto.OrderCreateRequest;
import com.xb.ddd.application.dto.OrderResponse;
import com.xb.ddd.domain.model.order.Order;
import com.xb.ddd.domain.model.order.OrderItem;
import com.xb.ddd.domain.model.shared.Address;
import com.xb.ddd.domain.model.shared.Money;
import com.xb.ddd.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 订单应用服务 —— 用例编排
 *
 * <p>作者：xb | 日期：2026-09-12</p>
 *
 * <p><b>DDD 知识点</b>：Application Service 是领域层的"门面"：
 * <ul>
 *   <li>接收 DTO，转换为领域对象</li>
 *   <li>调用领域方法执行业务逻辑</li>
 *   <li>管理事务和安全（@Transactional）</li>
 *   <li>调用 Repository 持久化</li>
 *   <li>不包含任何业务规则（业务在 domain 层）</li>
 * </ul></p>
 */
@Service
public class OrderAppService {

    private final OrderRepository orderRepository;
    private final AtomicLong idGen = new AtomicLong(1);

    public OrderAppService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /** 创建订单用例 */
    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        // 1. 装配值对象
        Address address = new Address(
            request.province(), request.city(),
            request.district(), request.detail());

        // 2. 创建订单项
        var items = request.items().stream()
            .map(i -> new OrderItem(items.indexOf(i) + 1,
                i.productId(), i.productName(),
                Money.rmb(i.unitPrice()), i.quantity()))
            .toList();

        // 3. 创建聚合根
        Order order = new Order(idGen.getAndIncrement(), request.userId(), address, items);

        // 4. 持久化
        orderRepository.save(order);

        return OrderResponse.from(order);
    }

    /** 查询订单 */
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long id) {
        return orderRepository.findById(id)
            .map(OrderResponse::from)
            .orElseThrow(() -> new RuntimeException("订单不存在"));
    }

    /** 支付订单 */
    @Transactional
    public void payOrder(Long id) {
        orderRepository.findById(id).ifPresent(order -> {
            order.pay();
            orderRepository.save(order);
        });
    }
}