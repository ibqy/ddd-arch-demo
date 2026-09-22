package com.xb.ddd.start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * DDD 教学项目 —— 启动类
 *
 * <p>作者：xb | 日期：2026-09-12</p>
 *
 * <p><b>DDD 知识点</b>：启动类扫描 {@code com.xb.ddd} 下的所有包，
 * 确保 infrastructure 中的 {@code @Repository} 和 interfaces 中的 {@code @RestController} 被注册。</p>
 *
 * @author ibqy
 */
@SpringBootApplication(scanBasePackages = "com.xb.ddd")
public class DddApplication {
    public static void main(String[] args) {
        SpringApplication.run(DddApplication.class, args);
    }
}