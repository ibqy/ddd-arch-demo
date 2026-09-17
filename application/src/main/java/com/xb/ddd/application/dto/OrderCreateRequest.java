package com.xb.ddd.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * 创建订单请求 DTO
 *
 * <p>作者：xb | 日期：2026-09-12</p>
 *
 * <p><b>DDD 知识点</b>：Application 层的 DTO 是"面向用例"的数据结构，
 * 与领域模型不同。DTO 传达"用户想做什么"，领域模型表达"业务是什么"。
 * 校验注解标注在 DTO 上，由接口层的 {@code @Valid} 触发。</p>
 */
public class OrderCreateRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "省份不能为空")
    private String province;

    @NotBlank(message = "城市不能为空")
    private String city;

    @NotBlank(message = "区县不能为空")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    private String detail;

    @NotEmpty(message = "订单项不能为空")
    @Valid
    private List<ItemRequest> items;

    public static class ItemRequest {
        @NotNull(message = "商品ID不能为空")
        private Long productId;

        @NotBlank(message = "商品名称不能为空")
        private String productName;

        @NotNull(message = "单价不能为空")
        @Positive(message = "单价必须大于0")
        private java.math.BigDecimal unitPrice;

        @Positive(message = "数量必须大于0")
        private int quantity;

        public Long productId() { return productId; }
        public String productName() { return productName; }
        public java.math.BigDecimal unitPrice() { return unitPrice; }
        public int quantity() { return quantity; }
        public void setProductId(Long v) { productId = v; }
        public void setProductName(String v) { productName = v; }
        public void setUnitPrice(java.math.BigDecimal v) { unitPrice = v; }
        public void setQuantity(int v) { quantity = v; }
    }

    public Long userId() { return userId; }
    public String province() { return province; }
    public String city() { return city; }
    public String district() { return district; }
    public String detail() { return detail; }
    public List<ItemRequest> items() { return items; }

    public void setUserId(Long v) { userId = v; }
    public void setProvince(String v) { province = v; }
    public void setCity(String v) { city = v; }
    public void setDistrict(String v) { district = v; }
    public void setDetail(String v) { detail = v; }
    public void setItems(List<ItemRequest> v) { items = v; }
}