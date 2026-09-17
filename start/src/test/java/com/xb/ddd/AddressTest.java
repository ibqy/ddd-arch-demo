package com.xb.ddd;

import com.xb.ddd.domain.model.shared.Address;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Address 值对象测试")
class AddressTest {

    @Test
    @DisplayName("创建地址")
    void createAddress() {
        Address addr = new Address("广东省", "深圳市", "南山区", "科技园C座");
        assertEquals("广东省", addr.province());
        assertEquals("深圳市", addr.city());
        assertEquals("南山区", addr.district());
        assertEquals("科技园C座", addr.detail());
    }

    @Test
    @DisplayName("withDetail 返回新实例")
    void withDetailReturnsNewInstance() {
        Address original = new Address("广东省", "深圳市", "南山区", "科技园C座");
        Address modified = original.withDetail("科技园D座");

        assertEquals("科技园C座", original.detail());
        assertEquals("科技园D座", modified.detail());
        assertNotSame(original, modified);
    }

    @Test
    @DisplayName("withDetail 保留其他字段")
    void withDetailPreservesOtherFields() {
        Address original = new Address("广东省", "深圳市", "南山区", "科技园C座");
        Address modified = original.withDetail("科技园D座");

        assertEquals("广东省", modified.province());
        assertEquals("深圳市", modified.city());
        assertEquals("南山区", modified.district());
    }

    @Test
    @DisplayName("sameCity 判断同一城市")
    void sameCity() {
        Address addr = new Address("广东省", "深圳市", "南山区", "科技园");
        assertTrue(addr.sameCity("深圳市"));
        assertFalse(addr.sameCity("广州市"));
    }

    @Test
    @DisplayName("相等性 - 所有属性相同则相等")
    void equalsAllFieldsSame() {
        Address a = new Address("广东省", "深圳市", "南山区", "科技园");
        Address b = new Address("广东省", "深圳市", "南山区", "科技园");
        assertEquals(a, b);
    }

    @Test
    @DisplayName("相等性 - 任一属性不同则不相等")
    void notEqualsAnyFieldDifferent() {
        Address a = new Address("广东省", "深圳市", "南山区", "科技园C座");
        Address b = new Address("广东省", "深圳市", "南山区", "科技园D座");
        assertNotEquals(a, b);
    }

    @Test
    @DisplayName("hashCode 一致")
    void hashCodeConsistent() {
        Address a = new Address("广东省", "深圳市", "南山区", "科技园");
        Address b = new Address("广东省", "深圳市", "南山区", "科技园");
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("toString 返回完整地址")
    void toStringFormat() {
        Address addr = new Address("广东省", "深圳市", "南山区", "科技园");
        assertEquals("广东省深圳市南山区科技园", addr.toString());
    }
}
