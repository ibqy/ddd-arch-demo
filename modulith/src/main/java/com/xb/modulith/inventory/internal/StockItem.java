package com.xb.modulith.inventory.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.util.Assert;

/**
 * 库存条目（inventory 模块内部实现）
 *
 * <p>DDD 知识点：
 * <ul>
 *     <li>以 productCode 为主键：一个商品一条库存记录（自然键聚合）</li>
 *     <li>decrease() 是聚合的公开行为，库存校验（不能扣成负数）内聚在聚合里</li>
 * </ul>
 *
 * @author xb
 * @date 2026-09-14
 */
@Entity
@Table(name = "stock_item")
public class StockItem {

    @Id
    @Column(name = "product_code")
    private String productCode;

    private int amount;

    /**
     * 仅供 JPA 使用
     */
    protected StockItem() {
    }

    /**
     * 创建库存记录（测试与初始化数据使用）
     */
    public StockItem(String productCode, int amount) {
        Assert.hasText(productCode, "商品编码不能为空");
        this.productCode = productCode;
        this.amount = amount;
    }

    /**
     * 扣减库存（守护"不能扣成负数"的不变量）
     */
    public void decrease(int quantity) {
        Assert.isTrue(quantity > 0, "扣减数量必须大于 0");
        Assert.isTrue(this.amount >= quantity,
                "库存不足：商品 " + productCode + " 当前 " + this.amount + "，需要 " + quantity);
        this.amount -= quantity;
    }

    public String productCode() {
        return productCode;
    }

    public int amount() {
        return amount;
    }
}
