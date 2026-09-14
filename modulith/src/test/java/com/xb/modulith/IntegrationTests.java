package com.xb.modulith;

import com.xb.modulith.inventory.InventoryQuery;
import com.xb.modulith.order.OrderManagement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 全流程集成测试：下单 → 完成 → 库存扣减
 *
 * <p>验证三个模块通过领域事件真正协作起来：
 * order 完成 → 事件发布注册表记录 → inventory 扣库存、notification 发通知。
 *
 * <p>注意 @ApplicationModuleListener 是 AFTER_COMMIT + 同步执行——
 * complete() 返回时，库存已经扣完，可以直接断言（无需 sleep）。
 *
 * @author xb
 * @date 2026-09-14
 */
@SpringBootTest
class IntegrationTests {

    @Autowired
    OrderManagement orders;

    @Autowired
    InventoryQuery inventory;

    @Test
    void completingOrderDecreasesStock() {
        // 1. 准备：下一单，记录下单前库存
        Long orderId = orders.place("SKU-001", 3);
        int before = inventory.currentAmount("SKU-001");
        // 2. 执行：完成订单（触发 OrderCompleted → 库存扣减 + 通知）
        orders.complete(orderId);
        // 3. 断言：库存扣了 3，订单状态是 COMPLETED
        assertThat(inventory.currentAmount("SKU-001")).isEqualTo(before - 3);
        assertThat(orders.findById(orderId).status()).isEqualTo("COMPLETED");
    }
}
