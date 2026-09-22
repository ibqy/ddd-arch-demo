package com.xb.ddd.application.dto;

import com.xb.ddd.domain.model.shared.Money;
import com.xb.ddd.domain.model.shared.OrderStatus;
import com.xb.ddd.domain.model.order.Order;
import com.xb.ddd.domain.model.order.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单响应 DTO —— 向外展示的数据
 *
 * <p>作者：xb | 日期：2026-09-12</p>
 *
 * <p><b>DDD 知识点</b>：响应 DTO 与领域模型一一对应，但视需要裁剪或聚合字段。
 * 某些敏感或不需暴露的领域属性（如内部版本号）不出现在 DTO 中。</p>
 *
 * @author ibqy
 */
public class OrderResponse {

    private Long orderId;
    private Long userId;
    private String address;
    private String status;
    private String totalAmount;
    private LocalDateTime createdAt;
    private List<ItemResponse> items;

    /**
     * 订单项响应 - 单行商品的展示数据
     *
     * 从 OrderItem 领域对象装配而来，
     * 金额序列化为字符串避免前端精度丢失。
     */
    public static class ItemResponse {
        private String productName;
        private String unitPrice;
        private int quantity;
        private String subtotal;

        public String productName() { return productName; }
        public String unitPrice() { return unitPrice; }
        public int quantity() { return quantity; }
        public String subtotal() { return subtotal; }
        public void setProductName(String v) { productName = v; }
        public void setUnitPrice(String v) { unitPrice = v; }
        public void setQuantity(int v) { quantity = v; }
        public void setSubtotal(String v) { subtotal = v; }
    }

    /** 从领域模型装配 */
    public static OrderResponse from(Order order) {
        var resp = new OrderResponse();
        resp.orderId = order.id();
        resp.userId = order.userId();
        resp.address = order.address().toString();
        resp.status = order.status().name();
        resp.totalAmount = order.totalAmount().amount().toPlainString();
        resp.createdAt = order.createdAt();
        resp.items = order.items().stream().map(item -> {
            var i = new ItemResponse();
            i.productName = item.productName();
            i.unitPrice = item.unitPrice().amount().toPlainString();
            i.quantity = item.quantity();
            i.subtotal = item.subtotal().amount().toPlainString();
            return i;
        }).toList();
        return resp;
    }

    public Long orderId() { return orderId; }
    public Long userId() { return userId; }
    public String address() { return address; }
    public String status() { return status; }
    public String totalAmount() { return totalAmount; }
    public LocalDateTime createdAt() { return createdAt; }
    public List<ItemResponse> items() { return items; }
}