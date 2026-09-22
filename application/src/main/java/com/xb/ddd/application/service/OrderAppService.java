package com.xb.ddd.application.service;

import com.xb.ddd.application.dto.OrderCreateRequest;
import com.xb.ddd.application.dto.OrderResponse;
import com.xb.ddd.common.exception.BizException;
import com.xb.ddd.domain.model.order.Order;
import com.xb.ddd.domain.model.order.OrderItem;
import com.xb.ddd.domain.model.shared.Address;
import com.xb.ddd.domain.model.shared.Money;
import com.xb.ddd.domain.repository.OrderRepository;
import com.xb.ddd.domain.service.OrderDomainService;
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
 *
 * @author ibqy
 */
@Service
public class OrderAppService {

    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;
    private final AtomicLong idGen = new AtomicLong(1);

    public OrderAppService(OrderRepository orderRepository, OrderDomainService orderDomainService) {
        this.orderRepository = orderRepository;
        this.orderDomainService = orderDomainService;
    }

    /**
     * 创建订单用例：装配值对象 → 创建聚合根 → 持久化 → 返回 DTO
     * @param request 创建订单请求 DTO
     * @return 订单响应 DTO
     */
    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        // 1. 装配值对象
        Address address = new Address(
            request.province(), request.city(),
            request.district(), request.detail());

        // 2. 创建订单项
        List<OrderItem> orderItems = new java.util.ArrayList<>();
        for (int idx = 0; idx < request.items().size(); idx++) {
            var i = request.items().get(idx);
            orderItems.add(new OrderItem(idx + 1,
                i.productId(), i.productName(),
                Money.rmb(i.unitPrice()), i.quantity()));
        }

        // 3. 创建聚合根
        Order order = new Order(idGen.getAndIncrement(), request.userId(), address, orderItems);

        // 4. 持久化
        orderRepository.save(order);

        return OrderResponse.from(order);
    }

    /**
     * 查询订单
     * @param id 订单 ID
     * @return 订单响应 DTO
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long id) {
        return orderRepository.findById(id)
            .map(OrderResponse::from)
            .orElseThrow(() -> new BizException(404, "订单不存在"));
    }

    /**
     * 支付订单
     * @param id 订单 ID
     */
    @Transactional
    public void payOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new BizException(404, "订单不存在"));
        order.pay();
        orderRepository.save(order);
    }

    /**
     * 申请退款（委托领域服务执行跨聚合逻辑）
     * @param id 订单 ID
     */
    @Transactional
    public void requestRefund(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new BizException(404, "订单不存在"));
        orderDomainService.requestRefund(order);
        orderRepository.save(order);
    }

    /**
     * 完成退款
     * @param id 订单 ID
     */
    @Transactional
    public void completeRefund(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new BizException(404, "订单不存在"));
        orderDomainService.completeRefund(order);
        orderRepository.save(order);
    }
}