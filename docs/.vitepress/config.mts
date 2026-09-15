import { defineConfig } from 'vitepress'

export default defineConfig({
  lang: 'zh-CN',
  title: 'DDD 架构教学',
  description: '以电商订单场景演示 DDD 四层架构与 Spring Modulith 对照实现',
  base: '/ddd-arch-demo/',
  lastUpdated: true,
  markdown: {
    config(md) {
      const defaultLink =
        md.renderer.rules.link_open ||
        ((tokens, idx, options, _env, self) => self.renderToken(tokens, idx, options))
      md.renderer.rules.link_open = (tokens, idx, options, env, self) => {
        const href = tokens[idx].attrGet('href')
        if (href && href.startsWith('../')) {
          const rel = href.replace(/^(\.\.\/)+/, '')
          const kind = /\.[A-Za-z]+$/.test(rel) ? 'blob' : 'tree'
          tokens[idx].attrSet('href', `https://github.com/ibqy/ddd-arch-demo/${kind}/main/${rel}`)
        }
        return defaultLink(tokens, idx, options, env, self)
      }
    }
  },
  themeConfig: {
    nav: [
      { text: '首页', link: '/' },
      { text: 'GitHub', link: 'https://github.com/ibqy/ddd-arch-demo' }
    ],
    sidebar: [
      {
        text: '教学文档',
        items: [
          { text: '01 · DDD 快速指南', link: '/01-ddd-quick-guide' },
          { text: '02 · Spring Modulith', link: '/02-spring-modulith-guide' }
        ]
      }
    ],
    socialLinks: [
      { icon: 'github', link: 'https://github.com/ibqy/ddd-arch-demo' }
    ],
    search: { provider: 'local' },
    outline: { level: [2, 3], label: '本页目录' },
    docFooter: { prev: '上一篇', next: '下一篇' },
    lastUpdated: { text: '最后更新于' },
    darkModeSwitchLabel: '外观',
    lightModeSwitchTitle: '切换到浅色模式',
    darkModeSwitchTitle: '切换到深色模式',
    sidebarMenuLabel: '文档',
    returnToTopLabel: '回到顶部',
    footer: {
      message: '个人教学项目 · 代码可跑 · 注释记录设计取舍',
      copyright: 'Copyright © 2026 ibqy'
    }
  }
})
