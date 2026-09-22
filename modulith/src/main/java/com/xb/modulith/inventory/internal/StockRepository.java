package com.xb.modulith.inventory.internal;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * StockRepository - 库存仓储（inventory 模块内部）
 *
 * 位于 internal 包，对外部模块隐藏。
 * 其他模块需查询库存时，应通过 InventoryQuery 门面访问，
 * 体现模块化设计中"不伸手进别人抽屉"的原则。
 *
 * @author ibqy
 */
public interface StockRepository extends JpaRepository<StockItem, String> {
}
