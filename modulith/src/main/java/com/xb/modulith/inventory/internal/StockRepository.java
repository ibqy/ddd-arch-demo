package com.xb.modulith.inventory.internal;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 库存仓储（模块内部）
 *
 * @author xb
 * @date 2026-09-14
 */
public interface StockRepository extends JpaRepository<StockItem, String> {
}
