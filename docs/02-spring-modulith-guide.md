# Spring Modulith 模块化指南

> 与主示例（四层架构）对照阅读：[01-ddd-quick-guide.md](01-ddd-quick-guide.md) 按**技术分层**组织代码，本模块按**业务能力**组织代码——同一个业务，两种切法。

## 为什么需要模块化？

单体应用最大的敌人不是"单体"本身，而是**大泥球（Big Ball of Mud）**：
所有代码互相引用，改一处动全身，最后谁也不敢重构。

- **微服务**：物理隔离，边界硬，但运维成本高（分布式事务、网络抖动、链路排查）
- **模块化单体（Modulith）**：逻辑隔离，边界靠工具守护，运维还是单体的简单

Spring Modulith 就是后者的官方实现：**先按模块把单体内务整理好，将来真要拆微服务时，按模块切割即可**。

## 架构总览

```
com.xb.modulith                     ← 主类位置（应用根包）
│
├── order/          【订单模块】      根包 = 公共 API
│   ├── OrderController             REST 入口
│   ├── OrderManagement             用例门面（place / complete）
│   ├── OrderCompleted              公共领域事件（record）
│   ├── OrderDetails                公共 DTO
│   └── internal/                   内部实现（对外隐藏）
│       ├── Order                   JPA 聚合根（registerEvent 发布事件）
│       ├── OrderStatus             状态枚举
│       └── OrderRepository         JPA 仓储
│
├── inventory/      【库存模块】
│   ├── OrderCompletedListener      @ApplicationModuleListener 扣库存
│   ├── InventoryQuery              公共查询门面
│   └── internal/
│       ├── StockItem               JPA 聚合（守护"不能扣成负数"）
│       └── StockRepository
│
└── notification/   【通知模块】
    └── OrderCompletedNotifier      @ApplicationModuleListener 发通知
```

一次"完成订单"的完整事件流：

```
   OrderManagement.complete(id)          ── 同步事务 ──
        │
        ▼
   Order.complete()                      状态 PLACED → COMPLETED
        │ registerEvent(OrderCompleted)   聚合根登记事件
        ▼
   repository.save(order)                Spring Data 自动发布事件
        │
   ──── 事务提交 ────                    EVENT_PUBLICATION 表记录
        │
        ▼ AFTER_COMMIT + REQUIRES_NEW
   ├── inventory：扣减库存（StockItem.decrease）
   └── notification：发送通知（日志演示）
```

## 两种组织方式对照

| 维度 | 主示例（四层架构） | 本模块（Spring Modulith） |
|------|-------------------|--------------------------|
| 切分依据 | 技术角色（接口/应用/领域/基础设施） | 业务能力（订单/库存/通知） |
| 包结构 | 每层一个 Maven 模块 | 每个业务一个包 = 应用模块 |
| 模块间通信 | 层间直接调用（application → domain） | 公共门面 + 领域事件 |
| 边界守护 | 依赖方向靠约定（Maven 依赖单向） | `verify()` 自动校验，违规即测试失败 |
| 适用场景 | 小项目快速起步，规则简单直观 | 业务多、团队多，需要防止互相侵入 |

> 两者不冲突：模块内部依然可以用四层思想（`internal` 里放聚合根就是领域层职责）。
> **先按业务切大块，再在大块内按技术切小块**，是常见组合。

## 模块一览

| 模块 | 角色 | 公共 API（根包） | 内部实现（internal/） |
|------|------|------------------|----------------------|
| **order** | 事件发布方 | `OrderController`、`OrderManagement`、`OrderCompleted`、`OrderDetails` | `Order`、`OrderStatus`、`OrderRepository` |
| **inventory** | 事件消费方 | `OrderCompletedListener`、`InventoryQuery` | `StockItem`、`StockRepository` |
| **notification** | 纯观察者 | `OrderCompletedNotifier` | — |

## 核心概念

### 1. 应用模块：主类包下的直接子包

无需任何注解声明，`ModulithApplication` 所在包的**每个直接子包**自动成为一个应用模块。

### 2. 根包 = 公共 API，internal/ = 内部实现

- 模块**根包**中的类型默认对外可见（Controller、门面服务、事件）
- **internal/ 子包**中的类型对外隐藏：其他模块引用它 → `verify()` 失败
- 想读别人的数据？敲正门（公共门面），不要伸手进 internal 抽屉

### 3. @ApplicationModuleListener：三合一注解

```java
@ApplicationModuleListener   // = @EventListener
void on(OrderCompleted event) //   + AFTER_COMMIT（主事务提交后才执行）
                              //   + REQUIRES_NEW（独立新事务执行）
```

为什么这么设计？订单事务成功提交前，扣库存毫无意义；而扣库存失败也不该回滚订单——失败的投递由事件注册表兜底。

### 4. 事件发布注册表（Event Publication Registry）

引入 `spring-modulith-starter-jpa` 后，每次事件发布都会记录到 `EVENT_PUBLICATION` 表：
消费成功 → 标记完成；消费失败/应用崩溃 → 记录保留，**重启后自动重新投递**。
这就是"本地事务 + 事件"的最终一致性（事务性发件箱 Transactional Outbox 思想的框架化实现）。

> 对照主示例的教学缺口：那边 `Order.popEvents()` 收集了领域事件却无人发布；
> 这边 `AbstractAggregateRoot.registerEvent()` + 注册表补全了"事件从产生到可靠送达"的完整链路。

## 三个演示点

### 演示点 ①：模块边界验证 —— [ModularityTests.java](../modulith/src/test/java/com/xb/modulith/ModularityTests.java)

```java
ApplicationModules modules = ApplicationModules.of(ModulithApplication.class);

@Test
void verifiesModularStructure() {
    modules.verify();   // 任何越界引用立即失败
}
```

试一试：在 `inventory` 的监听器里 import `com.xb.modulith.order.internal.Order`，
再跑这个测试——它会明确告诉你哪个包违规引用了哪个模块。
架构约束从"口头约定"变成"CI 里的红灯"。

### 演示点 ②：模块切片测试 —— [OrderModuleTests.java](../modulith/src/test/java/com/xb/modulith/order/OrderModuleTests.java)

```java
@ApplicationModuleTest              // 只引导 order 模块
class OrderModuleTests {

    @Test
    void publishesOrderCompletedEventOnCompletion(@Autowired PublishedEvents events) {
        Long orderId = orderManagement.place("SKU-001", 2);
        orderManagement.complete(orderId);

        assertThat(events.ofType(OrderCompleted.class)
                .matching(event -> event.orderId().equals(orderId)))
                .hasSize(1);        // 框架帮我们捕获并断言领域事件
    }
}
```

inventory / notification 的监听器**不会**被启动——测"订单发事件"，不需要真的扣库存。

### 演示点 ③：全流程集成 —— [IntegrationTests.java](../modulith/src/test/java/com/xb/modulith/IntegrationTests.java)

```java
Long orderId = orders.place("SKU-001", 3);
orders.complete(orderId);                       // 事务提交后同步扣库存
assertThat(inventory.currentAmount("SKU-001"))  // 100 → 97
        .isEqualTo(before - 3);
```

`@ApplicationModuleListener` 虽是 AFTER_COMMIT，但**同步执行**——complete() 返回时库存已扣完。

## 快速启动

```bash
# 前置：JDK 21+、Maven 3.9+（与主示例相同）
mvn -pl modulith spring-boot:run
# 启动后跑在 8081 端口（与主示例 8080 互不冲突）
```

验证事件链路：

```bash
# 1. 下单（返回订单 ID，如 1）
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{"productCode":"SKU-001","quantity":3}'

# 2. 完成订单 → 触发 OrderCompleted 事件 → 扣库存 + 发通知
curl -X POST http://localhost:8081/api/orders/1/complete

# 3. 查订单
curl http://localhost:8081/api/orders/1
# → {"id":1,"productCode":"SKU-001","quantity":3,"status":"COMPLETED"}

# 4. 查库存（公共查询门面演示：REST 之外，模块间也走正门）
#    打开 http://localhost:8081/h2-console（JDBC URL: jdbc:h2:mem:modulith）
#    SELECT * FROM STOCK_ITEM;      → SKU-001 的 AMOUNT 从 100 变 97
#    SELECT * FROM EVENT_PUBLICATION; → 看到已完成投递的事件记录
```

## 知识点速查

| 概念 | 对应代码 | 说明 |
|------|----------|------|
| **应用模块** | `order` / `inventory` / `notification` | 主类包的直接子包，零注解声明 |
| **公共 API** | `OrderManagement`、`InventoryQuery` | 模块根包类型，对外可见 |
| **内部实现** | `order.internal.Order` | internal 包类型，跨模块引用即违规 |
| **模块边界验证** | `ModularityTests#verifiesModularStructure` | `verify()` 让架构违规 = 测试失败 |
| **聚合根事件** | `Order#complete()` → `registerEvent(...)` | save() 时由 Spring Data 自动发布 |
| **模块事件监听** | `OrderCompletedListener` | `@ApplicationModuleListener` 三合一 |
| **事件发布注册表** | `EVENT_PUBLICATION` 表 | 失败投递自动重试（发件箱模式） |
| **模块切片测试** | `OrderModuleTests` | `@ApplicationModuleTest` 只引导单模块 |
| **文档生成** | `ModularityTests#writesDocumentation` | `Documenter` 输出 PlantUML 模块图 |

## 常见问题

**Q1：inventory 能直接调 order 的 OrderManagement 吗？**
能——`OrderManagement` 在 order 根包，是公共 API。本示例选择事件，是因为扣库存允许最终一致；
若需要同步拿到结果（如"下单时就要校验库存"），直接调门面更合适。

**Q2：扣库存时应用崩了，事件会丢吗？**
不会。`EVENT_PUBLICATION` 表记录了未完成的投递，重启后自动重投。
只有所有监听器都成功，记录才会标记完成。

**Q3：为什么 Controller 能引用 internal 的类，inventory 却不能？**
`OrderController` 和 `Order` 同属 order 模块（模块内自由引用）；
跨模块引用才受边界约束。**"家规管家人，不管外人"**。

**Q4：模块多了以后还能再分层吗？**
可以。模块内照样可以按 controller/service/repository 组织，
`internal/` 只是"这层防护墙从模块边界外移到了模块内"。

---

<p align="center">
  ← 返回 <a href="../README.md">README</a> · 上一篇 <a href="01-ddd-quick-guide.md">DDD 四层架构快速指南</a>
</p>
