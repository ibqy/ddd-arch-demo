package com.xb.modulith.notification;

import com.xb.modulith.order.OrderCompleted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.core.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * 订单完成通知（notification 模块）
 *
 * <p>最典型的"观察者型"业务模块：不提供任何服务，只订阅别人的事件。
 * 生产中这里可以接短信/邮件/站内信——本示例用日志代替，聚焦模块协作本身。
 *
 * <p>教学点：notification 依赖 order 的事件，但 order 对 notification
 * 一无所知——这就是事件驱动的"控制反转"：发布方反向解耦。
 *
 * @author xb
 * @date 2026-09-14
 */
@Component
public class OrderCompletedNotifier {

    private static final Logger log = LoggerFactory.getLogger(OrderCompletedNotifier.class);

    @ApplicationModuleListener
    void on(OrderCompleted event) {
        log.info("[通知模块] 订单 {} 已完成：商品 {} × {}，已向用户发送确认消息",
                event.orderId(), event.productCode(), event.quantity());
    }
}
