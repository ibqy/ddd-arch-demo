package com.xb.ddd.common.base;

/**
 * Identifier - 实体唯一标识的顶层抽象
 *
 * 在 DDD 中，实体靠身份区分而非属性值。
 * 所有聚合根的 ID 类型都实现此接口，
 * 使仓储和领域服务能以统一方式处理标识。
 *
 * @author ibqy
 */
public interface Identifier {
    /**
     * 获取标识的实际值
     * @return 唯一标识值
     */
    Long value();
}