package com.xb.modulith.order;

/**
 * 订单已完成事件（order 模块的公共领域事件）
 *
 * <p>放置在模块【根包】意味着：这是 order 模块对外发布的契约，
 * 其他模块（inventory、notification）允许监听它，但【不能】引用
 * order.internal 里的任何类型。
 *
 * <p>DDD 知识点：
 * <ul>
 *     <li>领域事件代表"已经发生的事实"，命名用过去式（OrderCompleted）</li>
 *     <li>事件是不可变的，用 record 实现（天然不可变 + 值相等）</li>
 *     <li>跨模块协作优先用事件解耦：order 不需要知道谁在消费</li>
 * </ul>
 *
 * <p>Spring Modulith 知识点：模块间的事件依赖被视为"最松耦合"的依赖形式，
 * 在生成的模块图中以虚线箭头呈现（对比直接类型引用的实线箭头）。
 *
 * @param orderId     订单 ID
 * @param productCode 商品编码
 * @param quantity    成交数量
 * @author xb
 * @date 2026-09-14
 */
public record OrderCompleted(Long orderId, String productCode, int quantity) {
}
