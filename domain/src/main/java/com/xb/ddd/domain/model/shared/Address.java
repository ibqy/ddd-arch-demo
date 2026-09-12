package com.xb.ddd.domain.model.shared;

import java.util.Objects;

/**
 * 地址 —— 值对象（Value Object）
 *
 * <p>作者：xb | 日期：2026-09-12</p>
 *
 * <p><b>DDD 知识点</b>：值对象无唯一标识，通过属性值判断相等性。
 * 一旦创建不可修改（final + getter only），修改时返回新实例。</p>
 *
 * <p><b>生产场景</b>：收货地址、金额、时间段、范围坐标等"描述性"概念。</p>
 */
public final class Address {

    private final String province;
    private final String city;
    private final String district;
    private final String detail;

    public Address(String province, String city, String district, String detail) {
        this.province = province;
        this.city = city;
        this.district = district;
        this.detail = detail;
    }

    /** 修改详细地址 → 返回新值对象 */
    public Address withDetail(String newDetail) {
        return new Address(this.province, this.city, this.district, newDetail);
    }

    /** 判断是否同一城市 */
    public boolean sameCity(String city) {
        return this.city.equals(city);
    }

    public String province() { return province; }
    public String city() { return city; }
    public String district() { return district; }
    public String detail() { return detail; }

    /** 值对象通过所有属性判断相等 */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address address)) return false;
        return Objects.equals(province, address.province)
            && Objects.equals(city, address.city)
            && Objects.equals(district, address.district)
            && Objects.equals(detail, address.detail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(province, city, district, detail);
    }

    @Override
    public String toString() {
        return province + city + district + detail;
    }
}