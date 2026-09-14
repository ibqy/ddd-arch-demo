# 🧱 DDD 领域驱动设计教学项目

> 作者：xb ｜ 日期：2026-09-12
>
> 一个基于"电商订单"业务场景的 DDD 四层架构实战项目，演示如何用领域驱动设计组织代码。
> **interfaces → application → domain → infrastructure**，每一层职责清晰、单向依赖。
>
> 另含 [Spring Modulith](modulith) 对照示例：同一业务按**业务能力**组织为
> order / inventory / notification 三个模块（[模块化指南](docs/02-spring-modulith-guide.md)）。

## 架构图

```
                          接口层（interfaces）
                      OrderController :8080
                              │
                          应用层（application）
                      OrderAppService（用例编排）
                              │
                    ┌─────────┴─────────┐
                    │    领域层（domain） │
                    │                   │
               ┌────┴────┐         ┌───┴────┐
               │ 值对象   │         │  实体   │
               │ Address  │         │ Order  │ ← 聚合根
               │ Money    │         │ OrderItem│
               │ OrderStatus        │        │
               └─────────┘         │ OrderRepository（接口）
                                   │ OrderDomainService
                                   │ OrderEvent（领域事件）
                    └──────────────┘
                              │
                       基础设施层（infrastructure）
                     InMemoryOrderRepository
```

## 模块一览

| 模块 | 层 | 核心内容 |
|------|-----|---------|
| [`domain`](domain) | 领域层 | Order（聚合根）、OrderItem、Address/Money（值对象）、OrderRepository 接口、领域事件 |
| [`application`](application) | 应用层 | OrderAppService（用例编排）、OrderCreateRequest/OrderResponse（DTO） |
| [`infrastructure`](infrastructure) | 基础设施 | InMemoryOrderRepository（仓储实现） |
| [`interfaces`](interfaces) | 接口层 | OrderController（REST API） |
| [`start`](start) | 启动 | Spring Boot 启动入口 |
| [`modulith`](modulith) | Spring Modulith 示例 | 按业务能力组织：order / inventory / notification，事件驱动协作 |

## 快速启动

```bash
# 前置：JDK 21+、Maven 3.9+

# 主示例：四层架构（8080 端口）
mvn -pl start spring-boot:run

# 对照示例：Spring Modulith 模块化（8081 端口）
mvn -pl modulith spring-boot:run
```

## 验证

```bash
# 创建订单
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId":1,
    "province":"广东省",
    "city":"深圳市",
    "district":"南山区",
    "detail":"科技园C座",
    "items":[{"productId":101,"productName":"机械键盘","unitPrice":399,"quantity":1}]
  }'
# → {"orderId":1,"userId":1,"address":"广东省深圳市南山区科技园C座","status":"CREATED",...}

# 查订单
curl http://localhost:8080/api/orders/1

# 支付
curl -X POST http://localhost:8080/api/orders/1/pay
```

### 对照示例：Spring Modulith（端口 8081）

```bash
# 下单（返回订单 ID，如 1）
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{"productCode":"SKU-001","quantity":3}'

# 完成订单 → 触发 OrderCompleted 事件 → 自动扣库存 + 发通知
curl -X POST http://localhost:8081/api/orders/1/complete

# 查订单
curl http://localhost:8081/api/orders/1
# → {"id":1,"productCode":"SKU-001","quantity":3,"status":"COMPLETED"}
```

## DDD 知识点速查

| 概念 | 对应代码 | 说明 |
|------|----------|------|
| **聚合根** | `Order` | 外部只能通过 Order 访问订单数据 |
| **实体** | `OrderItem` | 有局部 ID，属于 Order 聚合 |
| **值对象** | `Address`, `Money` | 无 ID、不可变、属性相等 |
| **领域事件** | `OrderCreatedEvent` | 已发生的业务事件 |
| **仓储接口** | `OrderRepository` | domain 层定义接口 |
| **仓储实现** | `InMemoryOrderRepository` | infrastructure 层实现 |
| **领域服务** | `OrderDomainService` | 跨聚合业务逻辑 |
| **应用服务** | `OrderAppService` | 用例编排、事务管理 |

## 目录结构

```
ddd-arch-demo/
├── pom.xml                          # 聚合父工程
├── common/                          # 公共模块（基类、异常）
│   └── src/main/java/com/xb/ddd/common/
├── domain/                          # 领域层（核心）
│   └── src/main/java/com/xb/ddd/domain/
│       ├── model/order/Order.java         # 聚合根
│       ├── model/order/OrderItem.java     # 实体
│       ├── model/shared/Address.java      # 值对象
│       ├── model/shared/Money.java        # 值对象
│       ├── model/shared/OrderStatus.java  # 枚举
│       ├── repository/OrderRepository.java
│       ├── service/OrderDomainService.java
│       └── event/OrderEvent.java
├── application/                     # 应用层
│   └── src/main/java/com/xb/ddd/application/
│       ├── service/OrderAppService.java
│       └── dto/OrderCreateRequest.java
├── infrastructure/                  # 基础设施层
│   └── src/main/java/com/xb/ddd/infrastructure/
│       └── repository/InMemoryOrderRepository.java
├── interfaces/                      # 接口层
│   └── src/main/java/com/xb/ddd/interfaces/
│       └── controller/OrderController.java
├── start/                           # 启动入口
├── modulith/                        # Spring Modulith 示例（order/inventory/notification）
│   └── src/main/java/com/xb/modulith/
├── docs/                            # 教学文档
└── README.md
```

## License

仅用于教学交流，作者：xb

<p align="center">
  <a href="https://github.com/ibqy">🏠 回到 ibqy 主页</a> · <a href="https://ibqy.github.io">🌐 作品集</a>
</p>
