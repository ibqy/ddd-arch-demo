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
                   GlobalExceptionHandler（异常 → HTTP 映射）
                   @Valid 参数校验（JSR-303）
                              │
                          应用层（application）
                   OrderAppService（用例编排 + BizException）
                   DTO + @NotNull/@NotBlank/@Positive 校验
                              │
                    ┌─────────┴─────────┐
                    │    领域层（domain） │
                    │                   │
               ┌────┴────┐         ┌───┴────┐
               │ 值对象   │         │  实体   │
               │ Address  │         │ Order  │ ← 聚合根
               │ Money    │         │ OrderItem│
               │ OrderStatus        │        │
               │ (状态机) │         │ OrderRepository（接口）
               └─────────┘         │ OrderDomainService
                                   │ 领域事件：OrderCreatedEvent
                                   │          OrderPaidEvent
                    └──────────────┘
                              │
                       基础设施层（infrastructure）
                     InMemoryOrderRepository
```

## 模块一览

| 模块 | 层 | 核心内容 |
|------|-----|---------|
| [`common`](common) | 公共 | BizException（业务异常）、Identifier（ID 基类） |
| [`domain`](domain) | 领域层 | Order（聚合根）、OrderItem、Address/Money（值对象）、OrderStatus（状态机）、OrderRepository 接口、OrderCreatedEvent/OrderPaidEvent（领域事件）、OrderDomainService |
| [`application`](application) | 应用层 | OrderAppService（用例编排 + BizException）、DTO + JSR-303 校验注解 |
| [`infrastructure`](infrastructure) | 基础设施 | InMemoryOrderRepository（仓储实现） |
| [`interfaces`](interfaces) | 接口层 | OrderController（REST API + 退款）、GlobalExceptionHandler（全局异常处理）、@Valid 参数校验 |
| [`start`](start) | 启动 | Spring Boot 启动入口、DomainConfig（领域服务 Bean 注册） |
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

# 申请退款
curl -X POST http://localhost:8080/api/orders/1/refund

# 完成退款
curl -X POST http://localhost:8080/api/orders/1/refund/complete

# 参数校验失败示例（userId 为空）
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId":null,"province":"","city":"","district":"","detail":"","items":[]}'
# → 400 {"code":400,"message":"...","time":"..."}
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
| **状态机** | `OrderStatus.canTransitTo()` | 枚举内封装合法状态转换，聚合根调用守护不变量 |
| **领域事件** | `OrderCreatedEvent`, `OrderPaidEvent` | 已发生的业务事件，聚合根内部收集 |
| **仓储接口** | `OrderRepository` | domain 层定义接口，不依赖框架 |
| **仓储实现** | `InMemoryOrderRepository` | infrastructure 层实现，可替换为 JPA/MyBatis |
| **领域服务** | `OrderDomainService` | 跨聚合业务逻辑，无 Spring 注解（领域层框架无关） |
| **Bean 注册** | `DomainConfig` | start 层 @Configuration 注册领域服务，保持 domain 纯净 |
| **应用服务** | `OrderAppService` | 用例编排、BizException 抛出 |
| **参数校验** | `@Valid` + JSR-303 注解 | 接口层入口校验，`@NotNull`/`@NotBlank`/`@Positive` |
| **全局异常处理** | `GlobalExceptionHandler` | BizException → 400，IllegalState → 409，领域异常不泄漏到客户端 |

## 目录结构

```
ddd-arch-demo/
├── pom.xml                          # 聚合父工程
├── common/                          # 公共模块（基类、异常）
│   └── src/main/java/com/xb/ddd/common/
│       ├── base/Identifier.java           # ID 基类
│       └── exception/BizException.java    # 业务异常（code + message）
├── domain/                          # 领域层（核心，无 Spring 依赖）
│   └── src/main/java/com/xb/ddd/domain/
│       ├── model/order/Order.java         # 聚合根（状态机 + 领域事件收集）
│       ├── model/order/OrderItem.java     # 实体
│       ├── model/shared/Address.java      # 值对象
│       ├── model/shared/Money.java        # 值对象
│       ├── model/shared/OrderStatus.java  # 枚举状态机（canTransitTo）
│       ├── repository/OrderRepository.java
│       ├── service/OrderDomainService.java # 跨聚合逻辑（纯 Java，无注解）
│       └── event/
│           ├── OrderEvent.java            # 事件标记接口
│           ├── OrderCreatedEvent.java     # 下单事件
│           └── OrderPaidEvent.java        # 支付事件
├── application/                     # 应用层
│   └── src/main/java/com/xb/ddd/application/
│       ├── service/OrderAppService.java   # 用例编排 + BizException
│       └── dto/
│           ├── OrderCreateRequest.java    # @Valid + JSR-303 校验
│           └── OrderResponse.java
├── infrastructure/                  # 基础设施层
│   └── src/main/java/com/xb/ddd/infrastructure/
│       └── repository/InMemoryOrderRepository.java
├── interfaces/                      # 接口层
│   └── src/main/java/com/xb/ddd/interfaces/
│       └── controller/
│           ├── OrderController.java       # REST API（含退款端点）
│           └── GlobalExceptionHandler.java # 全局异常 → HTTP 状态码映射
├── start/                           # 启动入口
│   └── src/main/java/com/xb/ddd/start/
│       ├── DddApplication.java          # @SpringBootApplication
│       └── DomainConfig.java            # @Configuration 注册领域服务 Bean
├── modulith/                        # Spring Modulith 示例（order/inventory/notification）
│   └── src/main/java/com/xb/modulith/
├── docs/                            # 教学文档
└── README.md
```

## 实现边界

### 已实现
- ✅ DDD 四层架构完整示例（interfaces → application → domain → infrastructure）
- ✅ 聚合根 Order + 枚举状态机（CREATED → PAID → SHIPPED → DELIVERED / REFUNDING → REFUNDED / CANCELLED）
- ✅ 值对象 Money / Address（不可变 + 相等性判断）
- ✅ 领域事件 OrderCreatedEvent + OrderPaidEvent（聚合根内部收集）
- ✅ 领域服务 OrderDomainService（退款校验，纯 Java 无 Spring 注解）
- ✅ DomainConfig @Configuration 注册领域服务（保持 domain 层框架无关）
- ✅ 应用服务 OrderAppService（用例编排 + BizException）
- ✅ 全局异常处理 GlobalExceptionHandler（BizException → 400，IllegalState → 409）
- ✅ JSR-303 参数校验（@Valid + @NotNull/@NotBlank/@Positive）
- ✅ 退款全流程（申请退款 → 完成退款，含状态机守护）
- ✅ 内存仓储 InMemoryOrderRepository
- ✅ Spring Modulith 对照示例（按业务能力组织）
- ✅ 59 个单元测试覆盖

### 教学简化
- 仓储使用内存实现（ConcurrentHashMap），生产环境替换为 JPA/MyBatis
- 无事务管理实际实现（@Transactional 仅声明）
- 无分布式事件发布（领域事件仅内存传递）

### 未实现
- ❌ 数据库持久化（JPA/MyBatis 映射）
- ❌ 分布式事件总线（Kafka/RabbitMQ）
- ❌ CQRS / Event Sourcing
- ❌ Saga 分布式事务
- ❌ 安全认证（Spring Security）

## 测试覆盖

```bash
mvn test -pl start
```

| 测试类 | 测试数 | 覆盖内容 |
|--------|--------|----------|
| MoneyTest | 8 | 值对象：创建、加法、乘法、负数校验、相等性、不可变性 |
| AddressTest | 8 | 值对象：创建、withDetail、sameCity、equals/hashCode |
| OrderItemTest | 4 | 实体：创建、小计计算 |
| OrderTest | 19 | 聚合根：状态机（含退款流转）、领域事件（含 OrderPaidEvent）、不可变列表、地址修改 |
| OrderDomainServiceTest | 6 | 领域服务：退款状态校验、完整退款流程 |
| OrderAppServiceTest | 10 | 应用服务：创建、查询、支付、退款全流程、BizException |
| InMemoryOrderRepositoryTest | 4 | 仓储：保存、查询、覆盖 |

## License

仅用于教学交流，作者：xb

<p align="center">
  <a href="https://github.com/ibqy">🏠 回到 ibqy 主页</a> · <a href="https://ibqy.github.io">🌐 作品集</a>
</p>
