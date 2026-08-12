-- H2 本地库初始化（MODE=MySQL）
-- 产品策略：一级分类为固定种子数据，二级分类可手动增删改

CREATE TABLE IF NOT EXISTS category_group (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL,
    icon        VARCHAR(10)  NOT NULL,
    color       VARCHAR(10)  NOT NULL,
    type        VARCHAR(10)  NOT NULL,
    sort_order  INT          DEFAULT 0,
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS category_sub (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_id    BIGINT       NOT NULL,
    name        VARCHAR(50)  NOT NULL,
    sort_order  INT          DEFAULT 0,
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES category_group(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS t_record (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    type        VARCHAR(10)  NOT NULL,
    amount      DECIMAL(12,2) NOT NULL,
    cat1_id     BIGINT       NOT NULL,
    cat2_id     BIGINT       NOT NULL,
    record_date DATE         NOT NULL,
    record_time TIME         NOT NULL,
    note        VARCHAR(500) DEFAULT '',
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cat1_id) REFERENCES category_group(id),
    FOREIGN KEY (cat2_id) REFERENCES category_sub(id)
);

CREATE INDEX IF NOT EXISTS idx_record_date ON t_record(record_date);
CREATE INDEX IF NOT EXISTS idx_record_type ON t_record(type);
CREATE INDEX IF NOT EXISTS idx_record_cat1 ON t_record(cat1_id);

-- 固定一级分类种子（仅首次插入）
MERGE INTO category_group (id, name, icon, color, type, sort_order) KEY(id) VALUES
(1,  '餐饮美食', '🍽', '#FF9F43', 'expense', 1),
(2,  '交通出行', '🚇', '#54A0FF', 'expense', 2),
(3,  '居家生活', '🏠', '#5F27CD', 'expense', 3),
(4,  '服饰美容', '👕', '#EE5A6F', 'expense', 4),
(5,  '健康医疗', '💊', '#26DE81', 'expense', 5),
(6,  '文教娱乐', '📚', '#0FBCF9', 'expense', 6),
(7,  '人情社交', '🎁', '#FD7E14', 'expense', 7),
(8,  '数码电子', '📱', '#A55EEA', 'expense', 8),
(9,  '金融保险', '💳', '#20BF6B', 'expense', 9),
(10, '其他支出', '📦', '#8898AA', 'expense', 10),
(11, '工资收入', '💰', '#00C48C', 'income', 1),
(12, '兼职收入', '💼', '#0FBCF9', 'income', 2),
(13, '投资收益', '📈', '#54A0FF', 'income', 3),
(14, '经营收入', '🏪', '#FD7E14', 'income', 4),
(15, '其他收入', '🎉', '#A55EEA', 'income', 5);

-- 默认二级分类（仅当该 group 下尚无子分类时插入）
INSERT INTO category_sub (group_id, name, sort_order)
SELECT * FROM (
    SELECT 1 AS group_id, '早餐' AS name, 1 AS sort_order UNION ALL
    SELECT 1, '午餐', 2 UNION ALL SELECT 1, '晚餐', 3 UNION ALL SELECT 1, '零食饮料', 4 UNION ALL
    SELECT 1, '外卖订餐', 5 UNION ALL SELECT 1, '聚餐应酬', 6 UNION ALL
    SELECT 2, '公交地铁', 1 UNION ALL SELECT 2, '打车网约车', 2 UNION ALL SELECT 2, '加油充电', 3 UNION ALL
    SELECT 2, '停车过路', 4 UNION ALL SELECT 2, '高铁火车', 5 UNION ALL SELECT 2, '飞机出行', 6 UNION ALL SELECT 2, '共享出行', 7 UNION ALL
    SELECT 3, '房租房贷', 1 UNION ALL SELECT 3, '水电燃气', 2 UNION ALL SELECT 3, '物业宽带', 3 UNION ALL
    SELECT 3, '日用百货', 4 UNION ALL SELECT 3, '家具家电', 5 UNION ALL SELECT 3, '装修维修', 6 UNION ALL
    SELECT 4, '服装鞋帽', 1 UNION ALL SELECT 4, '护肤化妆', 2 UNION ALL SELECT 4, '美发美甲', 3 UNION ALL SELECT 4, '饰品配件', 4 UNION ALL
    SELECT 5, '门诊就医', 1 UNION ALL SELECT 5, '药品购买', 2 UNION ALL SELECT 5, '体检保健', 3 UNION ALL SELECT 5, '看牙配镜', 4 UNION ALL
    SELECT 6, '书籍文具', 1 UNION ALL SELECT 6, '培训课程', 2 UNION ALL SELECT 6, '电影演出', 3 UNION ALL SELECT 6, '游戏娱乐', 4 UNION ALL SELECT 6, '旅行出游', 5 UNION ALL
    SELECT 7, '红包礼金', 1 UNION ALL SELECT 7, '请客吃饭', 2 UNION ALL SELECT 7, '礼物赠送', 3 UNION ALL
    SELECT 8, '手机电脑', 1 UNION ALL SELECT 8, '配件外设', 2 UNION ALL SELECT 8, '软件订阅', 3 UNION ALL SELECT 8, '数码维修', 4 UNION ALL
    SELECT 9, '保险费用', 1 UNION ALL SELECT 9, '信用卡还款', 2 UNION ALL SELECT 9, '贷款还款', 3 UNION ALL
    SELECT 10, '杂项支出', 1 UNION ALL
    SELECT 11, '基本工资', 1 UNION ALL SELECT 11, '绩效奖金', 2 UNION ALL SELECT 11, '加班费', 3 UNION ALL SELECT 11, '年终奖', 4 UNION ALL
    SELECT 12, '自由职业', 1 UNION ALL SELECT 12, '顾问咨询', 2 UNION ALL SELECT 12, '稿费收入', 3 UNION ALL SELECT 12, '其他兼职', 4 UNION ALL
    SELECT 13, '股票分红', 1 UNION ALL SELECT 13, '基金收益', 2 UNION ALL SELECT 13, '利息收入', 3 UNION ALL SELECT 13, '租金收入', 4 UNION ALL
    SELECT 14, '销售收入', 1 UNION ALL SELECT 14, '服务收入', 2 UNION ALL SELECT 14, '其他经营', 3 UNION ALL
    SELECT 15, '礼金红包', 1 UNION ALL SELECT 15, '报销退款', 2 UNION ALL SELECT 15, '中奖所得', 3 UNION ALL SELECT 15, '其他收入', 4
) s
WHERE NOT EXISTS (SELECT 1 FROM category_sub cs WHERE cs.group_id = s.group_id AND cs.name = s.name);

-- 自增起点不要写死 100：每次启动 reset 会撞上已有 id>=100 的用户数据
-- 由 SequenceResetRunner 在启动后按 MAX(id)+1 校准
