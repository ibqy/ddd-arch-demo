package com.xb.modulith.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.PublishedEvents;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * order 模块切片测试
 *
 * <p>使用 {@link ApplicationModuleTest} 只加载 order 模块，
 * 验证完成订单时 OrderCompleted 事件被正确发布。
 *
 * <p>教学要点：切片测试隔离了 inventory/notification 模块，
 * 证明模块可独立测试，无需全量启动。
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
