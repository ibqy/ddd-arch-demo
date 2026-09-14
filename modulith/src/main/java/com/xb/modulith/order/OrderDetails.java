package com.xb.modulith.order;

/**
 * 订单详情（order 模块对外的只读 DTO）
 *
 * <p>为什么不直接返回 internal 的 Order 实体？
 * —— 模块边界的要求：内部实现（JPA 实体）一旦直接暴露给外部，
 * 外部代码就会与你的表结构、懒加载策略耦合，模块化形同虚设。
 *
 * @param id          订单 ID
 * @param productCode 商品编码
 * @param quantity    数量
 * @param status      状态（PLACED / COMPLETED）
 * @author xb
 * @date 2026-09-14
 */
public record OrderDetails(Long id, String productCode, int quantity, String status) {
}
