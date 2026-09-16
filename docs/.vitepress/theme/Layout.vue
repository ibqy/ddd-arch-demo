<script setup>
import DefaultTheme from 'vitepress/theme'

const { Layout } = DefaultTheme
</script>

<template>
  <Layout>
    <template #home-features-before>
      <section class="sig">
        <div class="sig-card">
          <span class="sig-label">ARCHITECTURE · 4 LAYERS</span>
          <h3 class="sig-title">所有依赖，指向领域层</h3>
          <div class="dd-stack">
            <div class="dd-layer"><b>interfaces</b><span>用户接口层 · REST 接口与 DTO 转换</span></div>
            <div class="dd-layer"><b>application</b><span>应用层 · 用例编排，不含业务规则</span></div>
            <div class="dd-layer dd-domain"><b>domain</b><span>领域层 · 聚合根 Order / OrderItem · 值对象 Address / Money</span></div>
            <div class="dd-layer"><b>infrastructure</b><span>基础设施层 · 仓储实现，依赖倒置在此落地</span></div>
          </div>
          <div class="dd-badge">依赖方向：外层 → 内层 · Domain 不依赖任何框架<br>双实现对照：DDD 版 :8080 · Spring Modulith 版 :8081</div>
          <p class="sig-note">DDD 的核心不是分了四层，而是依赖指向哪——领域层干净了，业务才有地方沉淀。用电商订单把这个约束走一遍，比读十篇概念文章有用。</p>
        </div>
      </section>
    </template>
    <template #home-features-after>
      <div class="sig-more"><a href="https://ibqy.github.io/" target="_blank" rel="noopener">← ibqy.github.io · 探索全部项目</a></div>
    </template>
  </Layout>
</template>
