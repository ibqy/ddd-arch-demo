package com.xb.modulith.order.internal;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 订单仓储（模块内部，不对外暴露）
 *
 * <p>Spring Modulith 知识点：仓储在 internal 包中，
 * 其他模块想读订单只能通过 order 模块的公共门面（OrderManagement）。
 * 这与主示例"domain 定义接口 / infrastructure 实现"的分层思路殊途同归——
 * 一个按【技术分层】切，一个按【业务能力】切。
 *
 * @author xb
 * @date 2026-09-14
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
}
