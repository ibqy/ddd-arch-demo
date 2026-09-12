package com.xb.ddd.domain.model.shared;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * 金额 —— 值对象（Value Object）
 *
 * <p>作者：xb | 日期：2026-09-12</p>
 *
 * <p><b>DDD 知识点</b>：用值对象替代基本类型（BigDecimal），
 * 将金额+货币的校验逻辑封装在此，避免业务代码中分散的 null/负数检查。</p>
 */
public final class Money {

    private final BigDecimal amount;
    private final Currency currency;

    private Money(BigDecimal amount, Currency currency) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("金额不能为负");
        }
        this.amount = amount;
        this.currency = currency;
    }

    /** 工厂方法：人民币 */
    public static Money rmb(BigDecimal amount) {
        return new Money(amount, Currency.getInstance("CNY"));
    }

    /** 金额相加 */
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("币种不一致");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    /** 乘以数量 */
    public Money multiply(int quantity) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(quantity)), this.currency);
    }

    public BigDecimal amount() { return amount; }
    public Currency currency() { return currency; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money money)) return false;
        return amount.compareTo(money.amount) == 0
            && Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros(), currency);
    }
}