package com.xb.ddd;

import com.xb.ddd.application.dto.OrderCreateRequest;
import com.xb.ddd.application.dto.OrderResponse;
import com.xb.ddd.application.service.OrderAppService;
import com.xb.ddd.infrastructure.repository.InMemoryOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderAppService 应用服务测试")
class OrderAppServiceTest {

    private OrderAppService appService;

    @BeforeEach
    void setUp() {
        appService = new OrderAppService(new InMemoryOrderRepository());
    }

    @Test
    @DisplayName("创建订单 - 返回完整响应")
    void createOrder_returnsResponse() {
        OrderCreateRequest request = createRequest(1001L, "机械键盘", new BigDecimal("399"), 2);

        OrderResponse response = appService.createOrder(request);

        assertNotNull(response.orderId());
        assertEquals(1001L, response.userId());
        assertEquals("广东省深圳市南山区科技园", response.address());
        assertEquals("CREATED", response.status());
        assertEquals("798", response.totalAmount());
    }

    @Test
    @DisplayName("创建订单 - 多项商品")
    void createOrder_multipleItems() {
        OrderCreateRequest request = new OrderCreateRequest();
        request.setUserId(1001L);
        request.setProvince("广东省");
        request.setCity("深圳市");
        request.setDistrict("南山区");
        request.setDetail("科技园");

        OrderCreateRequest.ItemRequest item1 = new OrderCreateRequest.ItemRequest();
        item1.setProductId(101L);
        item1.setProductName("机械键盘");
        item1.setUnitPrice(new BigDecimal("399"));
        item1.setQuantity(1);

        OrderCreateRequest.ItemRequest item2 = new OrderCreateRequest.ItemRequest();
        item2.setProductId(102L);
        item2.setProductName("鼠标");
        item2.setUnitPrice(new BigDecimal("99"));
        item2.setQuantity(3);

        request.setItems(List.of(item1, item2));

        OrderResponse response = appService.createOrder(request);
        assertEquals("696", response.totalAmount());
    }

    @Test
    @DisplayName("查询订单 - 存在")
    void getOrder_exists() {
        OrderCreateRequest request = createRequest(1001L, "键盘", new BigDecimal("100"), 1);
        OrderResponse created = appService.createOrder(request);

        OrderResponse found = appService.getOrder(created.orderId());

        assertEquals(created.orderId(), found.orderId());
        assertEquals(created.userId(), found.userId());
    }

    @Test
    @DisplayName("查询订单 - 不存在抛出异常")
    void getOrder_notFound_throws() {
        assertThrows(RuntimeException.class, () -> appService.getOrder(999L));
    }

    @Test
    @DisplayName("支付订单")
    void payOrder() {
        OrderCreateRequest request = createRequest(1001L, "键盘", new BigDecimal("100"), 1);
        OrderResponse created = appService.createOrder(request);

        appService.payOrder(created.orderId());

        OrderResponse paid = appService.getOrder(created.orderId());
        assertEquals("PAID", paid.status());
    }

    @Test
    @DisplayName("订单 ID 自增")
    void orderIdAutoIncrement() {
        OrderCreateRequest request = createRequest(1001L, "键盘", new BigDecimal("100"), 1);

        OrderResponse first = appService.createOrder(request);
        OrderResponse second = appService.createOrder(request);

        assertTrue(second.orderId() > first.orderId());
    }

    private OrderCreateRequest createRequest(Long userId, String productName, BigDecimal price, int qty) {
        OrderCreateRequest request = new OrderCreateRequest();
        request.setUserId(userId);
        request.setProvince("广东省");
        request.setCity("深圳市");
        request.setDistrict("南山区");
        request.setDetail("科技园");

        OrderCreateRequest.ItemRequest item = new OrderCreateRequest.ItemRequest();
        item.setProductId(101L);
        item.setProductName(productName);
        item.setUnitPrice(price);
        item.setQuantity(qty);

        request.setItems(List.of(item));
        return request;
    }
}
