# 聚合根、值对象与领域事件

> 结论先行：DDD 不是"分四层包"，而是**把业务规则关进领域对象里**。
> 四层只是外壳，聚合根才是内核。

## 1. 聚合根：一切业务规则的入口

[Order](../domain/src/main/java/com/xb/ddd/domain/model/order/Order.java) 是订单聚合的唯一入口，它守三件事：

| 职责 | 实现方式 |
|:---|:---|
| 保护内部集合不被外部篡改 | `Collections.unmodifiableList(items)` |
| 维护冗余字段一致性 | 构造时用 `reduce(Money::add)` 算出 `totalAmount` |
| 约束状态流转 | `assertStatus` / `transitTo` 双重守卫 |

```java
public Order(Long id, Long userId, Address address, List<OrderItem> items) {
    this.items = Collections.unmodifiableList(items);   // 外部无法改列表
    this.status = OrderStatus.CREATED;
    this.totalAmount = items.stream()                     // 金额由明细推导
        .map(OrderItem::subtotal)
        .reduce(Money.rmb(BigDecimal.ZERO), Money::add);
    this.domainEvents.add(new OrderCreatedEvent(this.id, this.userId, this.totalAmount));
}
```

关键点：`totalAmount` 是**冗余字段**。冗余不可怕，可怕的是有人绕过领域方法直接改它。
把计算放进构造函数与业务方法里，冗余就变成了"缓存"，而不是"另一个真相"。

## 2. 状态守卫：两种写法，两种语义

[Order](../domain/src/main/java/com/xb/ddd/domain/model/order/Order.java) 里同时存在两种校验：

```java
/** 精确匹配：只允许从 CREATED 出发 */
private void assertStatus(OrderStatus expected) {
    if (this.status != expected) {
        throw new IllegalStateException("当前状态 " + this.status + " 不能执行该操作，需要 " + expected);
    }
}

/** 多源流转：由目标状态决定是否允许 */
private void transitTo(OrderStatus target) {
    if (!this.status.canTransitTo(target)) {
        throw new IllegalStateException("当前状态 " + this.status + " 不能转换到 " + target);
    }
    this.status = target;
}
```

为什么需要两种？

| 方法 | 语义 | 适用 |
|:---|:---|:---|
| `assertStatus(expected)` | "必须正好是某个状态" | `pay()` 只能从 `CREATED` 出发 |
| `transitTo(target)` | "多个状态都能到目标" | `requestRefund()` 允许 `PAID` 或 `SHIPPED` |

流转规则集中在 [OrderStatus.canTransitTo](../domain/src/main/java/com/xb/ddd/domain/model/shared/OrderStatus.java)：

```java
public boolean canTransitTo(OrderStatus target) {
    return switch (this) {
        case CREATED   -> target == PAID || target == CANCELLED;
        case PAID      -> target == SHIPPED || target == REFUNDING;
        case SHIPPED   -> target == DELIVERED || target == REFUNDING;
        case DELIVERED -> false;   // 终态
        case CANCELLED -> false;
        case REFUNDING -> target == REFUNDED || target == SHIPPED;
        case REFUNDED  -> false;
    };
}
```

用 `switch` 表达式而不是 `if-else` 链的好处：**编译器会强制你覆盖每个枚举值**，新增状态时不会漏。

## 3. 值对象：把校验前移到类型上

[Money](../domain/src/main/java/com/xb/ddd/domain/model/shared/Money.java) 是一个教科书式的值对象：

```java
public final class Money {
    private final BigDecimal amount;
    private final Currency currency;

    private Money(BigDecimal amount, Currency currency) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("金额不能为负");
        }
        ...
    }

    public static Money rmb(BigDecimal amount) { ... }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("币种不一致");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
}
```

四个特征全都有：

| 特征 | 体现 |
|:---|:---|
| 不可变 | `final` 字段 + 私有构造 + 运算返回新对象 |
| 无 ID | 用属性判断相等 |
| 自校验 | 不能为负、币种必须一致 |
| 行为内聚 | `add` / `multiply` 而不是暴露 `BigDecimal` 让外部算 |

> 对比：如果 `Order` 里直接放 `BigDecimal totalAmount`，那么"不能为负""币种一致"这些规则会散落在每个调用点。

`equals` 里用 `amount.compareTo(...) == 0` 而不是 `equals`，因为 `1.0` 与 `1.00` 在 `BigDecimal.equals` 下不相等——这是金额比较最经典的坑。

## 4. 领域事件：记录"已经发生的事实"

事件类用 `record`（天然不可变）：

```java
public record OrderCreatedEvent(Long orderId, Long userId, Money totalAmount) implements OrderEvent {}
public record OrderPaidEvent(Long orderId, Money totalAmount) implements OrderEvent {}
```

聚合根内部收集：

```java
private final List<OrderEvent> domainEvents = new ArrayList<>();

public void pay() {
    assertStatus(OrderStatus.CREATED);
    this.status = OrderStatus.PAID;
    this.domainEvents.add(new OrderPaidEvent(this.id, this.totalAmount));
}

public List<OrderEvent> popEvents() {
    var events = List.copyOf(this.domainEvents);
    this.domainEvents.clear();
    return events;
}
```

`popEvents()` 的语义是"取走并清空"——避免重复发布。

### 一个刻意的教学缺口

主示例里 `popEvents()` **没有任何调用方**。也就是说：事件被收集了，但从没被发布。

这不是 bug，而是留给读者的练习：

> **练习**：在 `application` 层的 `OrderAppService.payOrder()` 里，`save()` 之后调用 `popEvents()`，把事件交给 `ApplicationEventPublisher` 发布；再写一个监听器记录日志。

而 [Spring Modulith 模块](/02-spring-modulith-guide) 给出了"框架版答案"：`AbstractAggregateRoot.registerEvent()` + `repository.save()` 自动发布 + `@ApplicationModuleListener` 跨模块消费，连失败重投都由 `EVENT_PUBLICATION` 表兜住。

**先看手动版理解原理，再看框架版理解工程化**——这是本项目把两种实现放在一起的原因。

## 5. 仓储倒置：接口在领域，实现在基础设施

```java
// domain 层：只声明"我需要什么"
public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(Long id);
}
```

```java
// infrastructure 层：决定"怎么存"
@Repository
public class InMemoryOrderRepository implements OrderRepository {
    private final Map<Long, Order> store = new ConcurrentHashMap<>();
    ...
}
```

依赖方向：`infrastructure → domain`，而 `domain` 完全不知道 Spring、JPA、MyBatis 的存在。

这就是为什么 `domain` 模块的 `pom.xml` 里可以没有任何持久化框架依赖——**领域模型可以在纯 JUnit 下测试**。

## 6. 应用服务：只编排，不决策

[OrderAppService](../application/src/main/java/com/xb/ddd/application/service/OrderAppService.java) 的 `createOrder` 是标准四步：

```java
@Transactional
public OrderResponse createOrder(OrderCreateRequest request) {
    // 1. 装配值对象
    Address address = new Address(request.province(), request.city(), request.district(), request.detail());
    // 2. 创建实体
    List<OrderItem> orderItems = ...;
    // 3. 创建聚合根
    Order order = new Order(idGen.getAndIncrement(), request.userId(), address, orderItems);
    // 4. 持久化 + 转 DTO
    orderRepository.save(order);
    return OrderResponse.from(order);
}
```

注意这里**没有一行 if 判断业务规则**。所有规则都在 `Order` 与 `Money` 里。

```java
// 退款：委托领域服务，而不是自己在应用层判断
public void requestRefund(Long id) {
    Order order = orderRepository.findById(id).orElseThrow(...);
    orderDomainService.requestRefund(order);   // 业务判断在领域服务
    orderRepository.save(order);
}
```

划分标准一句话：

| 放哪里 | 判断依据 |
|:---|:---|
| 聚合根 | 只涉及**一个**聚合内的规则 |
| 领域服务 | 涉及**多个**聚合，或逻辑不属于任何实体 |
| 应用服务 | 编排用例、管理事务，**不含业务规则** |

> 本项目 `OrderDomainService` 目前只是转发调用，因为退款还没涉及"支付聚合"。等你要实现"退款金额不能超过已支付金额"时，它才真正有存在意义。

## 7. 怎么验证领域模型写对了

领域层零框架依赖，测试可以非常轻：

```java
@Test
void 已取消订单不能支付() {
    Order order = new Order(1L, 1L, address, items);
    order.cancel();
    assertThrows(IllegalStateException.class, order::pay);
}
```

判断标准：**如果测一个业务规则需要启动 Spring，说明规则放错层了。**

## 8. 小结

| 概念 | 本项目落点 | 记住一句话 |
|:---|:---|:---|
| 聚合根 | `Order` | 外部只能通过它改数据 |
| 值对象 | `Money` / `Address` | 把校验前移到类型上 |
| 状态机 | `OrderStatus.canTransitTo` | 规则集中，编译期强制覆盖 |
| 领域事件 | `OrderEvent` + `popEvents` | 记录事实，不描述意图 |
| 仓储倒置 | `OrderRepository` | 接口在 domain，实现在 infra |
| 应用服务 | `OrderAppService` | 编排不含规则 |

> 下一课：[02 · Spring Modulith 模块化](/02-spring-modulith-guide) —— 同一个业务，换一种切法：按业务能力而不是技术分层。
