# DDD 四层架构快速指南

## 架构总览

```
┌──────────────────────────────────────────────────┐
│              interfaces  （接口层）                │
│  OrderController — 处理 HTTP 请求/响应             │
├──────────────────────────────────────────────────┤
│              application （应用层）                │
│  OrderAppService — 用例编排、事务管理              │
├──────────────────────────────────────────────────┤
│               domain     （领域层）                │
│  Order（聚合根）→ OrderItem（实体）                 │
│  Address / Money（值对象）→ OrderStatus（枚举）     │
│  OrderRepository（接口）→ OrderDomainService       │
│  OrderCreatedEvent（领域事件）                     │
├──────────────────────────────────────────────────┤
│          infrastructure （基础设施层）              │
│  InMemoryOrderRepository — 仓储实现                │
└──────────────────────────────────────────────────┘
```

## 每一层包含什么

| 层 | 包含 | 依赖方向 |
|----|------|----------|
| **domain** | Entity, Value Object, Aggregate, Repository 接口, Domain Service, Domain Event | —（核心，零依赖） |
| **application** | AppService（无状态编排）、DTO | → domain |
| **infrastructure** | Repository 实现、ORM 映射、消息发送 | → application |
| **interfaces** | Controller、VO、Request/Response 转换 | → application |

## 核心概念

- **聚合根**：外部只通过聚合根操作，保证聚合内数据一致性
- **值对象**：无 ID、不可变、通过属性判断相等
- **仓储**：接口在 domain，实现在 infrastructure
- **领域事件**：记录"已发生"的业务事件，用于解耦