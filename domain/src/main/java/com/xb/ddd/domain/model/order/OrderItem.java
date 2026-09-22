package com.xb.ddd.domain.model.order;

import com.xb.ddd.domain.model.shared.Money;

/**
 * 订单项 —— 聚合内的实体（Entity in Aggregate）
 *
 * <p><b>DDD 知识点</b>：OrderItem 是 Order 聚合内的实体，拥有局部 ID（在订单内唯一），
 * 但它不属于聚合根，外部不能直接引用 OrderItem 的 ID 去操作它。
 * 所有对 OrderItem 的修改必须通过聚合根 Order 完成。</p>
 *
 * @author ibqy
 */
public class OrderItem {

    /** 订单项序号（聚合内唯一） */
    private final Integer seq;
    /** 商品 ID */
    private final Long productId;
    /** 商品名称 */
    private final String productName;
    /** 单价 */
    private final Money unitPrice;
    /** 数量 */
    private int quantity;

    /**
     * 创建订单项
     * @param seq 聚合内序号
     * @param productId 商品 ID
     * @param productName 商品名称
     * @param unitPrice 单价
     * @param quantity 数量
     */
    public OrderItem(Integer seq, Long productId, String productName, Money unitPrice, int quantity) {
        this.seq = seq;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    /** 计算小计金额 */
    public Money subtotal() {
        return unitPrice.multiply(quantity);
    }

    public Integer seq() { return seq; }
    public Long productId() { return productId; }
    public String productName() { return productName; }
    public Money unitPrice() { return unitPrice; }
    public int quantity() { return quantity; }
}