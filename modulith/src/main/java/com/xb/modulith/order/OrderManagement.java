package com.xb.modulith.order;

import com.xb.modulith.order.internal.Order;
import com.xb.modulith.order.internal.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单管理（order 模块的公共门面 / API）
 *
 * <p>模块内组件（本类、Controller）可以自由引用 internal 实现；
 * 模块外（inventory、notification）只能使用本模块根包的公共类型。
 *
 * <p>Spring Modulith 知识点：
 * <ul>
 *     <li>本类即 order 模块的"公共 API"——相当于模块的接口面</li>
 *     <li>complete() 内部由聚合根 registerEvent() 登记事件，
 *         repository.save() 时由 Spring Data 自动发布</li>
 *     <li>事件在事务提交后才被 @ApplicationModuleListener 消费（AFTER_COMMIT）</li>
 * </ul>
 *
 * <p>DDD 知识点：
 * <ul>
 *     <li>聚合根本身是"事件的事实来源"：状态变更与事件登记在同一个方法里发生</li>
 *     <li>对比主示例（start 模块）：Order.popEvents() 收集了事件却无人发布，
 *         本模块用 AbstractAggregateRoot + registerEvent() 补全了这一环</li>
 * </ul>
 *
 * <p>生产场景：跨模块协作走"公共门面 + 领域事件"两条路——
 * 需要同步结果的用门面方法，允许最终一致的通知类场景用事件。
 *
 * @author xb
 * @date 2026-09-14
 */
@Service
@Transactional
public class OrderManagement {

    private final OrderRepository repository;

    public OrderManagement(OrderRepository repository) {
        this.repository = repository;
    }

    /**
     * 下单：创建订单并持久化
     *
     * @return 订单 ID
     */
    public Long place(String productCode, int quantity) {
        // 1. 静态工厂方法创建聚合根，保证初始状态合法
        Order order = Order.place(productCode, quantity);
        // 2. save() 时 Spring Data 会发布聚合内 registerEvent() 登记的领域事件
        return repository.save(order).id();
    }

    /**
     * 完成订单：变更状态并触发 OrderCompleted 事件
     */
    public void complete(Long orderId) {
        // 1. 从数据库加载聚合根（此时 id 已存在，事件里携带的订单号有效）
        Order order = find(orderId);
        // 2. 状态流转 + 事件登记由聚合根统一把关
        order.complete();
        // 3. save() 时事件被真正发布，事务提交后由其他模块消费
        repository.save(order);
    }

    /**
     * 查询订单详情（对外只暴露 DTO，不泄漏 JPA 实体）
     */
    @Transactional(readOnly = true)
    public OrderDetails findById(Long orderId) {
        Order order = find(orderId);
        return new OrderDetails(order.id(), order.productCode(),
                order.quantity(), order.status().name());
    }

    private Order find(Long orderId) {
        return repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在：" + orderId));
    }
}
