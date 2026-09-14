package com.xb.modulith.inventory;

import com.xb.modulith.inventory.internal.StockItem;
import com.xb.modulith.inventory.internal.StockRepository;
import com.xb.modulith.order.OrderCompleted;
import org.springframework.modulith.core.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * 订单完成 → 扣减库存（inventory 模块对 order 事件的响应）
 *
 * <p>这是本示例最核心的教学点——{@link ApplicationModuleListener} 的三重含义：
 * <ul>
 *     <li>① @EventListener：监听 order 模块发布的 OrderCompleted 事件</li>
 *     <li>② AFTER_COMMIT：主事务（订单完成）提交成功后才执行——
 *         订单没提交成功就不扣库存，避免脏动作</li>
 *     <li>③ REQUIRES_NEW：在【独立新事务】中执行扣减——
 *         即使这里失败，也不影响已提交的订单</li>
 * </ul>
 *
 * <p>关键追问：如果扣库存时应用崩溃怎么办？
 * —— Spring Modulith 的【事件发布注册表】（EVENT_PUBLICATION 表）会记录
 * 未完成投递的事件，应用重启后自动重新投递，实现"本地事务 + 事件"的
 * 最终一致性（类似事务性发件箱 Transactional Outbox 模式）。
 *
 * <p>Spring Modulith 知识点：
 * <ul>
 *     <li>监听器放在 inventory 模块根包（公共区域），因为它需要引用
 *         order 模块的公共事件类型——internal 里的类不允许跨模块引用</li>
 *     <li>inventory → order 形成基于事件的依赖，比直接调用松得多</li>
 * </ul>
 *
 * @author xb
 * @date 2026-09-14
 */
@Component
public class OrderCompletedListener {

    private final StockRepository stockRepository;

    public OrderCompletedListener(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @ApplicationModuleListener
    void on(OrderCompleted event) {
        // 1. 加载商品库存（库存不足时抛异常 → 事件保持"未完成"，等待重投）
        StockItem item = stockRepository.findById(event.productCode())
                .orElseThrow(() -> new IllegalArgumentException("商品无库存记录：" + event.productCode()));
        // 2. 扣减库存（聚合自身守护"不能扣成负数"的不变量）
        item.decrease(event.quantity());
        // 3. 保存
        stockRepository.save(item);
    }
}
