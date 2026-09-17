package com.xb.ddd.start;

import com.xb.ddd.domain.service.OrderDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 领域层 Bean 注册
 *
 * <p>作者：xb | 日期：2026-09-12</p>
 *
 * <p><b>DDD 知识点</b>：领域层不依赖 Spring 注解（无 @Service/@Component），
 * 由基础设施层通过 @Configuration 将领域对象注册为 Spring Bean。
 * 这样领域层保持框架无关性，切换框架时只需修改这个配置类。</p>
 */
@Configuration
public class DomainConfig {

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainService();
    }
}
