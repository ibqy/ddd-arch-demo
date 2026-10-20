-- ============================================================
-- 初始库存数据（inventory 模块）
-- 作者：xb  |  日期：2026-09-14
-- ============================================================
-- 幂等插入：模块切片测试（@ApplicationModuleTest STANDALONE 模式）与完整应用上下文
-- 会共享同一个具名内存库（jdbc:h2:mem:modulith），data.sql 可能被执行多次，
-- 用 NOT EXISTS 守卫避免唯一键冲突（生产环境建议改用迁移工具或 DELETE+INSERT 策略）。
INSERT INTO stock_item(product_code, amount)
SELECT 'SKU-001', 100 WHERE NOT EXISTS (SELECT 1 FROM stock_item WHERE product_code = 'SKU-001');
INSERT INTO stock_item(product_code, amount)
SELECT 'SKU-002', 50 WHERE NOT EXISTS (SELECT 1 FROM stock_item WHERE product_code = 'SKU-002');
