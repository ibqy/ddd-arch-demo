package com.xb.modulith;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.documentation.Documenter;

/**
 * 模块边界验证测试（Spring Modulith 的"守门员"）
 *
 * <p>verify() 会扫描整个应用的模块结构，任何"越界引用"都会让测试失败，例如：
 * <ul>
 *     <li>inventory 模块的代码引用了 order.internal.Order（内部实体）</li>
 *     <li>跨模块循环依赖</li>
 * </ul>
 *
 * <p>生产场景：把它挂在 CI 上，架构约束就像单测一样自动执行——
 * "架构漂移"在提交阶段就被拦截，而不是靠口头约定。
 *
 * @author xb
 * @date 2026-09-14
 */
class ModularityTests {

    ApplicationModules modules = ApplicationModules.of(ModulithApplication.class);

    /**
     * 验证模块边界：任何违规引用立即失败
     */
    @Test
    void verifiesModularStructure() {
        modules.verify();
    }

    /**
     * 生成模块结构图（PlantUML）到 target/spring-modulith-docs/
     */
    @Test
    void writesDocumentation() {
        new Documenter(modules).writeModules();
    }
}
