package com.xb.modulith.inventory;

import com.xb.modulith.inventory.internal.StockItem;
import com.xb.modulith.inventory.internal.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 库存查询门面（inventory 模块的公共只读 API）
 *
 * <p>为什么需要它：StockRepository 在 internal/ 里，
 * 外部模块（以及集成测试）想知道库存时，不应该伸手进别人家抽屉，
 * 而是敲正门——通过模块提供的公共查询接口。
 *
 * @author xb
 * @date 2026-09-14
 */
@Service
@Transactional(readOnly = true)
public class InventoryQuery {

    private final StockRepository stockRepository;

    public InventoryQuery(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * 查询指定商品的当前库存量
     */
    public int currentAmount(String productCode) {
        return stockRepository.findById(productCode)
                .map(StockItem::amount)
                .orElse(0);
    }
}
