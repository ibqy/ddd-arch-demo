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
