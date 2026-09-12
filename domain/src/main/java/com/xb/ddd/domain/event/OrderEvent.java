package com.xb.ddd.domain.event;

import java.time.LocalDateTime;

/**
 * 领域事件 —— 基类（Domain Event）
 *
 * <p>作者：xb | 日期：2026-09-12</p>
 *
 * <p><b>DDD 知识点</b>：领域事件记录聚合中发生的"业务上有意义的事件"。
 * 事件是过去时（已发生不可撤销），用于解耦同一聚合内的不同关注点，
 * 或跨聚合/跨限界上下文的异步通知。</p>
 *
 * <p><b>生产场景</b>：订单创建→发积分、支付成功→通知发货、退款→通知财务。</p>
 */
public abstract class OrderEvent {

    /** 事件发生时间 */
    private final LocalDateTime occurredAt;

    protected OrderEvent() {
        this.occurredAt = LocalDateTime.now();
    }

    public LocalDateTime occurredAt() { return occurredAt; }
}