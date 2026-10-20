package com.xb.modulith.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.PublishedEvents;
import org.springframework.test.context.TestPropertySource;

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
 * <p><b>踩坑记录（2026-10-20 修复）</b>：单独运行本测试曾必失败
 * （{@code Table "STOCK_ITEM" not found}），全量跑却"碰巧"通过——
 * 因为同 JVM 先跑的集成测试已在共享具名内存库里建好表，典型的测试顺序假绿。
 * 根因：Modulith STANDALONE 切片上下文会覆盖 JPA 的 ddl-auto（不让 Hibernate 建表），
 * 而 data.sql 仍在脚本初始化阶段执行，表不存在。本测试只操作订单表、
 * 不依赖预置库存，因此用 {@code @TestPropertySource}（测试属性源优先级最高）
 * 显式声明：强制建表 + 跳过 data.sql。若将来测试需要库存数据，
 * 应在测试内通过 Repository 自行准备——测试数据自备是隔离性的基本要求。</p>
 *
 * @author xb
 * @date 2026-09-14
 */
@TestPropertySource(properties = {
        // 强制 Hibernate 建表（覆盖切片测试上下文对 ddl-auto 的默认处理）
        "spring.jpa.hibernate.ddl-auto=create-drop",
        // 本测试不需要 data.sql 的预置库存；且它先于建表执行会直接失败
        "spring.sql.init.mode=never"
})
@ApplicationModuleTest
class OrderModuleTests {

    @Autowired
    OrderManagement orderManagement;

    // 字段注入（而非方法参数）：JUnit 5.13 起对"多个 ParameterResolver 竞争同一参数"会直接报错，
    // SpringExtension 与 Modulith 的 PublishedEventsParameterResolver 会冲突；字段注入不受影响。
    @Autowired
    PublishedEvents events;

    @Test
    void publishesOrderCompletedEventOnCompletion() {
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
