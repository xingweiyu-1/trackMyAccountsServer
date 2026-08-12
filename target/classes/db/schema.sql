-- ============================================
-- 记账应用 数据库初始化脚本
-- ============================================

CREATE TABLE IF NOT EXISTS category_group (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL,
    icon        VARCHAR(10)  NOT NULL,
    color       VARCHAR(10)  NOT NULL,
    type        VARCHAR(10)  NOT NULL COMMENT 'expense / income',
    sort_order  INT          DEFAULT 0,
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS category_sub (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_id    BIGINT       NOT NULL,
    name        VARCHAR(50)  NOT NULL,
    sort_order  INT          DEFAULT 0,
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES category_group(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_record (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    type        VARCHAR(10)  NOT NULL COMMENT 'income / expense',
    amount      DECIMAL(12,2) NOT NULL,
    cat1_id     BIGINT       NOT NULL,
    cat2_id     BIGINT       NOT NULL,
    record_date DATE         NOT NULL,
    record_time TIME         NOT NULL,
    note        VARCHAR(500) DEFAULT '',
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cat1_id) REFERENCES category_group(id),
    FOREIGN KEY (cat2_id) REFERENCES category_sub(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IF NOT EXISTS idx_record_date ON t_record(record_date);
CREATE INDEX IF NOT EXISTS idx_record_type ON t_record(type);
CREATE INDEX IF NOT EXISTS idx_record_cat1 ON t_record(cat1_id);

-- ============================================
-- 种子数据：一级分类 + 二级分类
-- ============================================

-- 支出分类
INSERT INTO category_group (id, name, icon, color, type, sort_order) VALUES
(1,  '餐饮美食', '🍽', '#FF9F43', 'expense', 1),
(2,  '交通出行', '🚇', '#54A0FF', 'expense', 2),
(3,  '居家生活', '🏠', '#5F27CD', 'expense', 3),
(4,  '服饰美容', '👕', '#EE5A6F', 'expense', 4),
(5,  '健康医疗', '💊', '#26DE81', 'expense', 5),
(6,  '文教娱乐', '📚', '#0FBCF9', 'expense', 6),
(7,  '人情社交', '🎁', '#FD7E14', 'expense', 7),
(8,  '数码电子', '📱', '#A55EEA', 'expense', 8),
(9,  '金融保险', '💳', '#20BF6B', 'expense', 9),
(10, '其他支出', '📦', '#8898AA', 'expense', 10);

INSERT INTO category_sub (group_id, name, sort_order) VALUES
(1, '早餐',1), (1, '午餐',2), (1, '晚餐',3), (1, '零食饮料',4), (1, '外卖订餐',5), (1, '聚餐应酬',6),
(2, '公交地铁',1), (2, '打车网约车',2), (2, '加油充电',3), (2, '停车过路',4), (2, '高铁火车',5), (2, '飞机出行',6), (2, '共享出行',7),
(3, '房租房贷',1), (3, '水电燃气',2), (3, '物业宽带',3), (3, '日用百货',4), (3, '家具家电',5), (3, '装修维修',6),
(4, '服装鞋帽',1), (4, '护肤化妆',2), (4, '美发美甲',3), (4, '饰品配件',4),
(5, '门诊就医',1), (5, '药品购买',2), (5, '体检保健',3), (5, '看牙配镜',4),
(6, '书籍文具',1), (6, '培训课程',2), (6, '电影演出',3), (6, '游戏娱乐',4), (6, '旅行出游',5),
(7, '红包礼金',1), (7, '请客吃饭',2), (7, '礼物赠送',3),
(8, '手机电脑',1), (8, '配件外设',2), (8, '软件订阅',3), (8, '数码维修',4),
(9, '保险费用',1), (9, '信用卡还款',2), (9, '贷款还款',3),
(10, '杂项支出',1);

-- 收入分类
INSERT INTO category_group (id, name, icon, color, type, sort_order) VALUES
(11, '工资收入', '💰', '#00C48C', 'income', 1),
(12, '兼职收入', '💼', '#0FBCF9', 'income', 2),
(13, '投资收益', '📈', '#54A0FF', 'income', 3),
(14, '经营收入', '🏪', '#FD7E14', 'income', 4),
(15, '其他收入', '🎉', '#A55EEA', 'income', 5);

INSERT INTO category_sub (group_id, name, sort_order) VALUES
(11, '基本工资',1), (11, '绩效奖金',2), (11, '加班费',3), (11, '年终奖',4),
(12, '自由职业',1), (12, '顾问咨询',2), (12, '稿费收入',3), (12, '其他兼职',4),
(13, '股票分红',1), (13, '基金收益',2), (13, '利息收入',3), (13, '租金收入',4),
(14, '销售收入',1), (14, '服务收入',2), (14, '其他经营',3),
(15, '礼金红包',1), (15, '报销退款',2), (15, '中奖所得',3), (15, '其他收入',4);

-- 重置自增ID
ALTER TABLE category_group AUTO_INCREMENT = 100;
ALTER TABLE category_sub AUTO_INCREMENT = 100;
ALTER TABLE t_record AUTO_INCREMENT = 1;
