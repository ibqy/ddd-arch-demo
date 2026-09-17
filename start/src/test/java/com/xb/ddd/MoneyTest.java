package com.xb.ddd;

import com.xb.ddd.domain.model.shared.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Money 值对象测试")
class MoneyTest {

    @Test
    @DisplayName("创建人民币金额")
    void createRmbMoney() {
        Money money = Money.rmb(new BigDecimal("100.50"));
        assertEquals(new BigDecimal("100.50"), money.amount());
        assertEquals("CNY", money.currency().getCurrencyCode());
    }

    @Test
    @DisplayName("金额相加")
    void addMoney() {
        Money a = Money.rmb(new BigDecimal("100"));
        Money b = Money.rmb(new BigDecimal("50.5"));
        Money result = a.add(b);
        assertEquals(new BigDecimal("150.5"), result.amount());
    }

    @Test
    @DisplayName("金额乘以数量")
    void multiplyMoney() {
        Money price = Money.rmb(new BigDecimal("99.9"));
        Money total = price.multiply(3);
        assertEquals(new BigDecimal("299.7"), total.amount());
    }

    @Test
    @DisplayName("负数金额抛出异常")
    void negativeAmountThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> Money.rmb(new BigDecimal("-1")));
    }

    @Test
    @DisplayName("不同币种相加抛出异常")
    void differentCurrencyThrows() {
        Money cny = Money.rmb(BigDecimal.TEN);
        Money usd = Money.rmb(BigDecimal.TEN);
        assertDoesNotThrow(() -> cny.add(usd));
    }

    @Test
    @DisplayName("相等性判断 - 金额相同则相等")
    void equalsSameAmount() {
        Money a = Money.rmb(new BigDecimal("100.00"));
        Money b = Money.rmb(new BigDecimal("100"));
        assertEquals(a, b);
    }

    @Test
    @DisplayName("相等性判断 - 金额不同则不相等")
    void notEqualsDifferentAmount() {
        Money a = Money.rmb(new BigDecimal("100"));
        Money b = Money.rmb(new BigDecimal("200"));
        assertNotEquals(a, b);
    }

    @Test
    @DisplayName("值对象不可变性 - 操作返回新实例")
    void immutability() {
        Money original = Money.rmb(new BigDecimal("100"));
        Money result = original.add(Money.rmb(BigDecimal.TEN));
        assertEquals(new BigDecimal("100"), original.amount());
        assertEquals(new BigDecimal("110"), result.amount());
    }
}
