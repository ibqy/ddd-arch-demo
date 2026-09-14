package com.xb.modulith;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Modulith 模块化示例启动入口
 *
 * <p>与主示例（start 模块）最大的区别：这里【没有】scanBasePackages——
 * Spring Modulith 的模块边界正是建立在"主类所在包 + 直接子包"之上的：
 *
 * <pre>
 * com.xb.modulith          ← 主类位置（应用根包）
 * ├── order/               ← 模块①：订单（根包 = 公共 API，internal/ = 内部实现）
 * ├── inventory/           ← 模块②：库存
 * └── notification/        ← 模块③：通知
 * </pre>
 *
 * <p>Spring Modulith 知识点：
 * <ul>
 *     <li>应用模块 = 主类包下的【直接子包】，无需任何注解声明</li>
 *     <li>模块根包中的类型（Controller、事件、门面服务）默认对外可见</li>
 *     <li>internal/ 子包中的类型对外隐藏，跨模块引用会被 verify() 判定违规</li>
 * </ul>
 *
 * <p>生产场景：中大型单体应用既想保持"一个可部署单元"的简单运维，
 * 又想避免代码演化成大泥球（Big Ball of Mud），Spring Modulith 提供了
 * "逻辑微服务"的折中方案，未来拆分微服务时按模块切割即可。
 *
 * @author xb
 * @date 2026-09-14
 */
@SpringBootApplication
public class ModulithApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModulithApplication.class, args);
    }
}
