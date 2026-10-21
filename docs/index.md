---
layout: home

hero:
  name: DDD 架构教学
  text: 电商订单场景双实现对照
  tagline: 以电商订单演示 DDD 四层架构：聚合根 Order/OrderItem、值对象 Address/Money、依赖倒置、领域事件，并与 Spring Modulith 对照实现
  actions:
    - theme: brand
      text: 开始学习 →
      link: /01-ddd-quick-guide
    - theme: alt
      text: GitHub 源码
      link: https://github.com/ibqy/ddd-arch-demo

features:
  - icon: 🏛️
    title: 四层架构
    details: interfaces / application / domain / infrastructure 各层职责与依赖方向
  - icon: 🧱
    title: 聚合根与值对象
    details: Order 聚合根管理 OrderItem，Address / Money 值对象不可变建模
  - icon: 🔄
    title: 依赖倒置
    details: 领域层定义仓储接口，基础设施层实现——Domain 不依赖任何框架
  - icon: 📢
    title: 领域事件
    details: 订单创建等业务事件解耦后续流程，事件即业务事实
  - icon: 🧪
    title: 架构守护测试
    details: Spring Modulith ModularityTests 自动验证模块边界不被破坏
  - icon: ▶️
    title: 双实现对照
    details: DDD 版 8080 / Modulith 版 8081 同时运行，同一场景两种风格对比
---

## 课程结构

| 章节 | 主题 | 关键代码 |
|:---|:---|:---|
| [01 · DDD 快速指南](/01-ddd-quick-guide) | 四层职责、依赖方向、核心概念 | — |
| [03 · 聚合根与领域事件](/03-aggregate-domain-events) | 聚合不变量、值对象、状态机、事件收集 | `Order` / `Money` / `OrderStatus` / `OrderEvent` |
| [02 · Spring Modulith](/02-spring-modulith-guide) | 按业务能力切分、模块边界验证、事件注册表 | `OrderManagement` / `OrderCompletedListener` / `ModularityTests` |

> 建议顺序：先读 01 建立全局认识，再读 03 深入聚合内部，最后读 02 看另一种组织方式。

## 代码地图

| 模块 | 关键类 | 职责 |
|:---|:---|:---|
| `domain` | `Order` / `Money` / `Address` / `OrderRepository` | 聚合根、值对象、仓储接口（零框架依赖） |
| `application` | `OrderAppService` | 用例编排与事务边界 |
| `infrastructure` | `InMemoryOrderRepository` | 仓储实现（可替换为 JPA / MyBatis） |
| `interfaces` | `OrderController` | HTTP 入口与 DTO 转换 |
| `modulith` | `OrderManagement` / `OrderCompletedListener` | 模块化单体的公共门面与事件消费 |

## 在路线中的位置

> **Java × AI 进阶路线** · 阶段 1「架构与资金安全」第 1 站  
> 上一站：[MyBatis-Plus 从零到原理](https://ibqy.github.io/mybatis-plus-demo/) ｜ 下一站：[Spring Cloud 微服务](https://ibqy.github.io/spring-cloud-micro-demo/)  
> 完整路线：[ibqy.github.io/curriculum](https://ibqy.github.io/curriculum)

## 启动

```bash
mvn -pl start spring-boot:run      # DDD 四层版，8080
mvn -pl modulith spring-boot:run   # Modulith 版，8081
```
