package com.xb.ddd.domain.repository;

import com.xb.ddd.domain.model.order.Order;

import java.util.Optional;

/**
 * 订单仓储接口 —— 领域层定义，基础设施层实现
 *
 * <p>作者：xb | 日期：2026-09-12</p>
 *
 * <p><b>DDD 知识点</b>：Repository 接口定义在 domain 层（属于领域），
 * 具体实现在 infrastructure 层。这样 domain 层不依赖任何框架或数据库细节。
 * 应用层只通过接口操作聚合，不知道底层是 MySQL、MongoDB 还是内存。</p>
 */
public interface OrderRepository {

    /** 保存聚合（新增或更新） */
    void save(Order order);

    /** 根据 ID 查找聚合 */
    Optional<Order> findById(Long id);
}