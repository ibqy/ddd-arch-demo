package com.xb.modulith.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.PublishedEvents;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * order 模块切片测试
 *
 * <p>{@link ApplicationModuleTest} 只引导【当前模块】：
 * 模块内的 bean（OrderManagement、JPA 仓储）正常加载，
 * 但 inventory / notification 的监听器【不会】被启动——
 * 测试"完成订单会发布事件"这件事，不需要真的扣库存。
 *
 * <p>Spring Modulith 知识点：
 * <ul>
 *     <li>切片测试让"模块 = 可独立测试单元"从口号变成现实</li>
 *     <li>PublishedEvents 是框架提供的测试工具，捕获本次测试中发布的所有领域事件</li>
 * </ul>
 *
 * @author xb
 * @date 2026-09-14
 */
@ApplicationModuleTest
class OrderModuleTests {

    @Autowired
    OrderManagement orderManagement;

    @Test
    void publishesOrderCompletedEventOnCompletion(@Autowired PublishedEvents events) {
        // 1. 准备：下一单
        Long orderId = orderManagement.place("SKU-001", 2);
        // 2. 执行：完成订单
        orderManagement.complete(orderId);
        // 3. 断言：OrderCompleted 事件恰好发布一次，且内容正确
        assertThat(events.ofType(OrderCompleted.class)
                .matching(event -> event.orderId().equals(orderId)))
                .hasSize(1);
    }
}
