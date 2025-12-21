/*
 Navicat Premium Dump SQL

 Source Server         : 2023mysql
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : lnforumfinal

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 20/12/2025 14:11:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `category_id` int NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态：0禁用，1启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`category_id`) USING BTREE,
  INDEX `idx_sort`(`sort_order` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '动态', '普通校园动态', 1, 1, '2025-12-09 14:32:58');
INSERT INTO `category` VALUES (2, '跑腿', '代取快递、代送物品等跑腿服务', 2, 1, '2025-12-09 14:32:58');
INSERT INTO `category` VALUES (3, '二手集市', '二手物品交易市场', 3, 1, '2025-12-09 14:32:58');
INSERT INTO `category` VALUES (4, '失物招领', '寻找丢失物品和发布失物信息', 4, 1, '2025-12-09 14:32:58');
INSERT INTO `category` VALUES (5, '招聘', '校园招聘和兼职信息', 5, 1, '2025-12-09 14:32:58');
INSERT INTO `category` VALUES (6, '部门动态', '学校部门通知和公告', 6, 1, '2025-12-09 14:32:58');

-- ----------------------------
-- Table structure for follow
-- ----------------------------
DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow`  (
  `follow_id` int NOT NULL AUTO_INCREMENT COMMENT '关注ID',
  `follower_id` int NOT NULL COMMENT '关注者ID',
  `following_id` int NOT NULL COMMENT '被关注者ID',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态：0拉黑，1未拉黑',
  PRIMARY KEY (`follow_id`) USING BTREE,
  UNIQUE INDEX `uk_follower_following`(`follower_id` ASC, `following_id` ASC) USING BTREE,
  INDEX `idx_follower`(`follower_id` ASC) USING BTREE,
  INDEX `idx_following`(`following_id` ASC) USING BTREE,
  CONSTRAINT `follow_ibfk_1` FOREIGN KEY (`follower_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `follow_ibfk_2` FOREIGN KEY (`following_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '关注/关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of follow
-- ----------------------------
INSERT INTO `follow` VALUES (1, 2, 3, 1);
INSERT INTO `follow` VALUES (2, 3, 2, 1);
INSERT INTO `follow` VALUES (3, 4, 2, 1);
INSERT INTO `follow` VALUES (4, 2, 5, 1);

-- ----------------------------
-- Table structure for interaction
-- ----------------------------
DROP TABLE IF EXISTS `interaction`;
CREATE TABLE `interaction`  (
  `interaction_id` int NOT NULL AUTO_INCREMENT COMMENT '互动ID',
  `post_id` int NOT NULL COMMENT '动态ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `interaction_type` enum('like','comment','favorite') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '互动类型',
  `comment_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '评论内容',
  `parent_id` int NULL DEFAULT NULL COMMENT '父评论ID',
  `comment_status` enum('正常','已删除','违规') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '正常' COMMENT '评论子状态',
  `comment_like_count` int NULL DEFAULT 0 COMMENT '评论点赞数',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态：0删除，1没删',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`interaction_id`) USING BTREE,
  UNIQUE INDEX `uk_post_user_type`(`post_id` ASC, `user_id` ASC, `interaction_type` ASC) USING BTREE,
  INDEX `idx_post_type`(`post_id` ASC, `interaction_type` ASC) USING BTREE,
  INDEX `idx_user_type`(`user_id` ASC, `interaction_type` ASC) USING BTREE,
  INDEX `idx_post_comment`(`post_id` ASC, `interaction_type` ASC, `create_time` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_interaction_composite`(`post_id` ASC, `interaction_type` ASC, `status` ASC, `create_time` DESC) USING BTREE,
  CONSTRAINT `interaction_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `interaction_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `interaction_ibfk_3` FOREIGN KEY (`parent_id`) REFERENCES `interaction` (`interaction_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '互动表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of interaction
-- ----------------------------
INSERT INTO `interaction` VALUES (1, 1, 3, 'like', NULL, NULL, NULL, 0, 1, '2025-12-09 10:00:00', '2025-12-09 10:00:00');
INSERT INTO `interaction` VALUES (2, 1, 4, 'like', NULL, NULL, NULL, 0, 1, '2025-12-09 10:05:00', '2025-12-09 10:05:00');
INSERT INTO `interaction` VALUES (3, 2, 2, 'like', NULL, NULL, NULL, 0, 1, '2025-12-09 10:30:00', '2025-12-09 10:30:00');
INSERT INTO `interaction` VALUES (4, 1, 3, 'comment', '确实，我早上7点去就已经没位置了', NULL, '正常', 2, 1, '2025-12-09 10:10:00', '2025-12-09 10:10:00');
INSERT INTO `interaction` VALUES (5, 1, 2, 'comment', '@李小红 可以去教学楼的空教室', 4, '正常', 1, 1, '2025-12-09 10:15:00', '2025-12-09 10:15:00');
INSERT INTO `interaction` VALUES (6, 2, 4, 'comment', '一起加油！我也在备考', NULL, '正常', 0, 1, '2025-12-09 10:40:00', '2025-12-09 10:40:00');
INSERT INTO `interaction` VALUES (7, 2, 2, 'favorite', NULL, NULL, NULL, 0, 1, '2025-12-09 11:00:00', '2025-12-09 11:00:00');
INSERT INTO `interaction` VALUES (8, 6, 3, 'favorite', NULL, NULL, NULL, 0, 1, '2025-12-09 14:30:00', '2025-12-09 14:30:00');

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `message_id` int NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` int NULL DEFAULT NULL COMMENT '发送者ID（NULL表示系统消息）',
  `receiver_id` int NOT NULL COMMENT '接收者ID',
  `message_type` tinyint(1) NULL DEFAULT 0 COMMENT '类型：0私信，1系统通知',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '消息标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `msg_format` tinyint(1) NULL DEFAULT 0 COMMENT '格式：0text，1img',
  `image_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片地址',
  `related_type` enum('用户','帖子','评论','订单','任务','举报') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关联类型(保留作为参考)',
  `is_read` tinyint(1) NULL DEFAULT 0 COMMENT '是否已读',
  `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `idx_receiver_type`(`receiver_id` ASC, `message_type` ASC) USING BTREE,
  INDEX `idx_receiver_read`(`receiver_id` ASC, `is_read` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` DESC) USING BTREE,
  INDEX `message_ibfk_1`(`sender_id` ASC) USING BTREE,
  CONSTRAINT `message_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `message_ibfk_2` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES (1, 2, 3, 0, NULL, '', 0, NULL, '用户', 1, '2025-12-09 12:30:00', '2025-12-09 12:20:00');
INSERT INTO `message` VALUES (2, 3, 2, 0, NULL, '最低70，已经是最低价了', 0, NULL, '用户', 1, '2025-12-09 12:35:00', '2025-12-09 12:25:00');
INSERT INTO `message` VALUES (3, NULL, 2, 1, '欢迎加入校园社区', '欢迎张小明加入我们的校园社区，请遵守社区规则', 0, NULL, '用户', 1, '2025-12-09 15:05:00', '2025-12-09 15:00:00');
INSERT INTO `message` VALUES (4, NULL, 3, 1, '您的商品有人咨询', '用户张小明咨询了您的考研资料，请及时回复', 0, NULL, '帖子', 0, NULL, '2025-12-09 12:20:00');

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post`  (
  `post_id` int NOT NULL AUTO_INCREMENT COMMENT '动态ID',
  `user_id` int NOT NULL COMMENT '发布者ID',
  `category_id` int NOT NULL COMMENT '分类ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '动态内容',
  `contact_info` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '隐私信息',
  `deadline` datetime NULL DEFAULT NULL COMMENT '截至时间',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `item_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '物品信息',
  `start_point` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '起点',
  `end_point` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '终点',
  `image1` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片1',
  `image2` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片2',
  `image3` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片3',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态：0待审核, 1已通过, 2已删除, 3已结束',
  `view_count` int NULL DEFAULT 0 COMMENT '查看次数',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
  `favorite_count` int NULL DEFAULT 0 COMMENT '收藏数',
  `trending_level` tinyint(1) NULL DEFAULT 0 COMMENT '热度：0普通, 1热门',
  `review` tinyint(1) NULL DEFAULT 0 COMMENT '是否需要审核',
  `review_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`post_id`) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_category_time`(`category_id` ASC, `create_time` ASC) USING BTREE,
  INDEX `idx_status_time`(`status` ASC, `create_time` ASC) USING BTREE,
  INDEX `idx_trending_time`(`trending_level` ASC, `create_time` DESC) USING BTREE,
  CONSTRAINT `post_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `post_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '动态表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of post
-- ----------------------------
INSERT INTO `post` VALUES (1, 2, 1, '今天图书馆人真多', '期末复习季，图书馆一座难求，大家加油！', NULL, NULL, NULL, NULL, NULL, NULL, 'lib1.jpg', NULL, NULL, 1, 156, 23, 8, 5, 0, 0, '2025-12-09 09:30:00', '2025-12-09 09:30:00', '2025-12-09 14:00:00');
INSERT INTO `post` VALUES (2, 3, 1, '考研倒计时30天', '最后一个月冲刺，研友们一起加油！', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 289, 45, 12, 18, 1, 0, '2025-12-09 10:15:00', '2025-12-09 10:15:00', '2025-12-09 16:20:00');
INSERT INTO `post` VALUES (3, 2, 2, '代取快递', '下午3-5点有时间，可代取中通快递，小件3元，大件5元', '微信: zhxm123', '2025-12-09 17:00:00', 5.00, NULL, '菜鸟驿站', '宿舍7号楼', NULL, NULL, NULL, 1, 78, 5, 3, 2, 0, 0, '2025-12-09 11:20:00', '2025-12-09 11:20:00', '2025-12-09 11:20:00');
INSERT INTO `post` VALUES (4, 3, 3, '出售考研资料', '2025年考研数学一全套资料，几乎全新', '电话: 13800138002', '2025-12-15 00:00:00', 80.00, '包含真题、模拟题、笔记', NULL, NULL, 'book1.jpg', 'book2.jpg', NULL, 1, 134, 12, 6, 7, 0, 0, '2025-12-09 12:00:00', '2025-12-09 12:00:00', '2025-12-09 12:00:00');
INSERT INTO `post` VALUES (5, 4, 4, '捡到一卡通', '在二食堂门口捡到一张学生卡，姓名:李华', '电话: 13800138003', NULL, NULL, '蓝色卡套，信息工程学院', NULL, NULL, 'card1.jpg', NULL, NULL, 1, 45, 3, 2, 1, 0, 0, '2025-12-09 13:10:00', '2025-12-09 13:10:00', '2025-12-09 13:10:00');
INSERT INTO `post` VALUES (6, 5, 5, '招聘研究助理', '计算机学院实验室招聘研究助理2名，要求熟悉Python', '邮箱: wang@university.edu', '2025-12-20 00:00:00', NULL, '每周工作10-15小时，有科研经验者优先', NULL, NULL, NULL, NULL, NULL, 1, 210, 18, 9, 15, 1, 0, '2025-12-09 14:00:00', '2025-12-09 14:00:00', '2025-12-09 14:00:00');
INSERT INTO `post` VALUES (7, 1, 6, '校园网络维护通知', '12月10日00:00-06:00校园网络维护，期间可能无法访问', NULL, '2025-12-10 06:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 320, 28, 5, 12, 0, 0, '2025-12-09 14:30:00', '2025-12-09 14:30:00', '2025-12-09 14:30:00');

-- ----------------------------
-- Table structure for post_tag
-- ----------------------------
DROP TABLE IF EXISTS `post_tag`;
CREATE TABLE `post_tag`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `post_id` int NOT NULL COMMENT '动态ID',
  `tag_id` int NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_post_tag`(`post_id` ASC, `tag_id` ASC) USING BTREE,
  INDEX `idx_tag`(`tag_id` ASC) USING BTREE,
  INDEX `idx_post`(`post_id` ASC) USING BTREE,
  CONSTRAINT `post_tag_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `post_tag_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`tag_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '动态-标签关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of post_tag
-- ----------------------------
INSERT INTO `post_tag` VALUES (1, 1, 2);
INSERT INTO `post_tag` VALUES (2, 2, 1);
INSERT INTO `post_tag` VALUES (3, 2, 2);
INSERT INTO `post_tag` VALUES (4, 4, 1);
INSERT INTO `post_tag` VALUES (5, 4, 3);
INSERT INTO `post_tag` VALUES (6, 6, 4);
INSERT INTO `post_tag` VALUES (7, 6, 10);

-- ----------------------------
-- Table structure for report
-- ----------------------------
DROP TABLE IF EXISTS `report`;
CREATE TABLE `report`  (
  `report_id` int NOT NULL AUTO_INCREMENT COMMENT '举报ID',
  `reporter_id` int NOT NULL COMMENT '举报人ID',
  `target_type` enum('用户','帖子','评论') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '举报目标类型',
  `target_id` int NOT NULL COMMENT '举报目标ID',
  `report_type` enum('垃圾信息','色情低俗','违法违规','欺诈','侵权','其他') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '举报类型',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '举报描述',
  `report_image` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '举报图片地址',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态：0待处理, 1已处理',
  `admin_id` int NULL DEFAULT NULL COMMENT '处理管理员ID',
  `result` tinyint(1) NULL DEFAULT NULL COMMENT '结果：0未通过, 1通过',
  `feedback` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理反馈',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',
  `process_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`report_id`) USING BTREE,
  UNIQUE INDEX `uk_reporter_target`(`reporter_id` ASC, `target_type` ASC, `target_id` ASC) USING BTREE,
  INDEX `admin_id`(`admin_id` ASC) USING BTREE,
  INDEX `idx_status_time`(`status` ASC, `create_time` ASC) USING BTREE,
  INDEX `idx_target`(`target_type` ASC, `target_id` ASC) USING BTREE,
  CONSTRAINT `report_ibfk_1` FOREIGN KEY (`reporter_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `report_ibfk_2` FOREIGN KEY (`admin_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '举报表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of report
-- ----------------------------
INSERT INTO `report` VALUES (1, 2, '帖子', 1, '其他', '这个帖子描述的情况不实，图书馆今天人很少', NULL, 1, 1, 0, '经核实，帖子内容属实', '2025-12-09 11:00:00', '2025-12-09 14:00:00');
INSERT INTO `report` VALUES (2, 3, '评论', 5, '其他', '这个回复带有攻击性', NULL, 0, NULL, NULL, NULL, '2025-12-09 11:30:00', NULL);

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `tag_id` int NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`tag_id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '标签表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag` VALUES (1, '考研', '2025-12-09 14:32:58');
INSERT INTO `tag` VALUES (2, '学习', '2025-12-09 14:32:58');
INSERT INTO `tag` VALUES (3, '二手', '2025-12-09 14:32:58');
INSERT INTO `tag` VALUES (4, '兼职', '2025-12-09 14:32:58');
INSERT INTO `tag` VALUES (5, '租房', '2025-12-09 14:32:58');
INSERT INTO `tag` VALUES (6, '美食', '2025-12-09 14:32:58');
INSERT INTO `tag` VALUES (7, '运动', '2025-12-09 14:32:58');
INSERT INTO `tag` VALUES (8, '社团', '2025-12-09 14:32:58');
INSERT INTO `tag` VALUES (9, '快递', '2025-12-09 14:32:58');
INSERT INTO `tag` VALUES (10, '招聘', '2025-12-09 14:32:58');

-- ----------------------------
-- Table structure for trade_task
-- ----------------------------
DROP TABLE IF EXISTS `trade_task`;
CREATE TABLE `trade_task`  (
  `task_id` int NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `task_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务编号',
  `post_id` int NOT NULL COMMENT '关联动态ID',
  `creator_id` int NOT NULL COMMENT '创建者ID',
  `acceptor_id` int NULL DEFAULT NULL COMMENT '接受者ID',
  `task_type` enum('二手交易','跑腿服务') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务类型',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '任务描述',
  `amount` decimal(10, 2) NOT NULL COMMENT '金额/报酬',
  `start_location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '起点',
  `end_location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '终点',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  `estimated_time` int NULL DEFAULT NULL COMMENT '预计耗时(分钟)',
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `task_status` tinyint(1) NULL DEFAULT 0 COMMENT '状态：0进行中, 1已完成, 2已取消',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '取消原因',
  PRIMARY KEY (`task_id`) USING BTREE,
  UNIQUE INDEX `task_no`(`task_no` ASC) USING BTREE,
  UNIQUE INDEX `post_id`(`post_id` ASC) USING BTREE,
  INDEX `idx_task_status`(`task_status` ASC) USING BTREE,
  INDEX `idx_trade_task_composite`(`task_type` ASC, `task_status` ASC, `create_time` DESC) USING BTREE,
  INDEX `trade_task_ibfk_2`(`creator_id` ASC) USING BTREE,
  INDEX `trade_task_ibfk_3`(`acceptor_id` ASC) USING BTREE,
  CONSTRAINT `trade_task_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `trade_task_ibfk_2` FOREIGN KEY (`creator_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `trade_task_ibfk_3` FOREIGN KEY (`acceptor_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '交易任务表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of trade_task
-- ----------------------------
INSERT INTO `trade_task` VALUES (1, 'TR202512090001', 4, 3, 2, '二手交易', '购买考研资料', '2025年考研数学一全套资料', 70.00, NULL, NULL, '包含真题、模拟题、笔记', NULL, '李小红', '13800138002', 1, '2025-12-09 13:00:00', '2025-12-09 13:30:00', NULL, NULL);
INSERT INTO `trade_task` VALUES (2, 'ER202512090001', 3, 2, 4, '跑腿服务', '代取快递', '代取中通快递包裹', 5.00, '菜鸟驿站', '宿舍7号楼', '小件包裹', 20, '张小明', '13800138001', 0, '2025-12-09 13:30:00', '2025-12-09 13:30:00', NULL, NULL);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `phone` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'default_avatar.png' COMMENT '头像URL',
  `gender` tinyint(1) NULL DEFAULT 0 COMMENT '性别：0未知, 1男, 2女',
  `signature` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '个性签名',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址',
  `role` tinyint(1) NULL DEFAULT 3 COMMENT '角色：0超级管理员, 1管理员, 2跑腿员, 3普通用户',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态：0正常, 1禁用, 2注销',
  `warning_count` int NULL DEFAULT 0 COMMENT '警告次数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `phone`(`phone` ASC) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', 'Admin User', 'admin123', '13800138000', NULL, 'admin_avatar.png', 0, NULL, NULL, 0, 0, 0, '2025-12-09 14:32:58', NULL);
INSERT INTO `user` VALUES (2, '张小明', '张小明', '$2a$10$abc123', '13800138001', 'zhxm@example.com', 'avatar1.jpg', 1, '热爱学习的程序员', '北京市海淀区', 3, 0, 0, '2025-12-09 15:00:00', '2025-12-09 18:30:00');
INSERT INTO `user` VALUES (3, '李小红', '李小红', '$2a$10$def456', '13800138002', 'lixh@example.com', 'avatar2.jpg', 2, '考研进行中', '北京市朝阳区', 3, 0, 0, '2025-12-09 15:05:00', '2025-12-09 19:20:00');
INSERT INTO `user` VALUES (4, '跑腿小王', '王五', '$2a$10$ghi789', '13800138003', 'paitw@example.com', 'avatar3.jpg', 1, '专业跑腿，安全快捷', '北京市丰台区', 2, 0, 0, '2025-12-09 15:10:00', '2025-12-09 20:15:00');
INSERT INTO `user` VALUES (5, '王老师', '王建国', '$2a$10$jkl012', '13800138004', 'wangls@example.com', 'avatar4.jpg', 1, '计算机系教师', '北京市西城区', 3, 0, 0, '2025-12-09 15:15:00', '2025-12-09 17:45:00');
INSERT INTO `user` VALUES (6, '校园记者', '赵六', '$2a$10$mno345', '13800138005', 'xyjz@example.com', 'avatar5.jpg', 2, '校园新闻第一时间', '北京市东城区', 3, 0, 1, '2025-12-09 15:20:00', '2025-12-09 16:30:00');

-- ----------------------------
-- Table structure for user_ban_history
-- ----------------------------
DROP TABLE IF EXISTS `user_ban_history`;
CREATE TABLE `user_ban_history`  (
  `history_id` int NOT NULL AUTO_INCREMENT COMMENT '历史ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `admin_id` int NOT NULL COMMENT '操作管理员ID',
  `action_type` enum('封禁','解封') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型',
  `restrictions_before` json NULL COMMENT '封禁前权限',
  `restrictions_after` json NULL COMMENT '封禁后权限',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作原因',
  `duration_days` int NULL DEFAULT 0 COMMENT '封禁天数（0为永久）',
  `start_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否生效',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`history_id`) USING BTREE,
  INDEX `admin_id`(`admin_id` ASC) USING BTREE,
  INDEX `idx_user_active`(`user_id` ASC, `is_active` ASC) USING BTREE,
  INDEX `idx_start_time`(`start_time` ASC) USING BTREE,
  INDEX `idx_end_time`(`end_time` ASC) USING BTREE,
  CONSTRAINT `user_ban_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_ban_history_ibfk_2` FOREIGN KEY (`admin_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户封禁历史表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_ban_history
-- ----------------------------
INSERT INTO `user_ban_history` VALUES (1, 6, 1, '封禁', '{\"can_buy\": 1, \"can_like\": 1, \"can_post\": 1, \"can_sell\": 1, \"can_follow\": 1, \"can_comment\": 1, \"can_message\": 1, \"can_run_errand\": 1}', '{\"can_buy\": 1, \"can_like\": 1, \"can_post\": 0, \"can_sell\": 1, \"can_follow\": 1, \"can_comment\": 1, \"can_message\": 1, \"can_run_errand\": 1}', '发布虚假广告信息', 7, '2025-12-09 15:30:00', '2025-12-16 15:30:00', 1, '2025-12-09 15:30:00');

-- ----------------------------
-- Table structure for user_permission
-- ----------------------------
DROP TABLE IF EXISTS `user_permission`;
CREATE TABLE `user_permission`  (
  `permission_id` int NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `can_post` tinyint(1) NULL DEFAULT 1 COMMENT '是否可以发帖',
  `can_comment` tinyint(1) NULL DEFAULT 1 COMMENT '是否可以评论',
  `can_like` tinyint(1) NULL DEFAULT 1 COMMENT '是否可以点赞',
  `can_follow` tinyint(1) NULL DEFAULT 1 COMMENT '是否可以关注',
  `can_message` tinyint(1) NULL DEFAULT 1 COMMENT '是否可以发送私信',
  `can_buy` tinyint(1) NULL DEFAULT 1 COMMENT '是否可以购买',
  `can_sell` tinyint(1) NULL DEFAULT 1 COMMENT '是否可以出售',
  `can_run_errand` tinyint(1) NULL DEFAULT 1 COMMENT '是否可以跑腿',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`permission_id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_can_post`(`can_post` ASC) USING BTREE,
  INDEX `idx_can_comment`(`can_comment` ASC) USING BTREE,
  CONSTRAINT `user_permission_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_permission
-- ----------------------------
INSERT INTO `user_permission` VALUES (1, 1, 1, 1, 1, 1, 1, 1, 1, 1, '2025-12-09 15:29:50');
INSERT INTO `user_permission` VALUES (2, 2, 1, 1, 1, 1, 1, 1, 1, 1, '2025-12-09 15:29:50');
INSERT INTO `user_permission` VALUES (3, 3, 1, 1, 1, 1, 1, 1, 1, 1, '2025-12-09 15:29:50');
INSERT INTO `user_permission` VALUES (4, 4, 1, 1, 1, 1, 1, 1, 1, 1, '2025-12-09 15:29:50');
INSERT INTO `user_permission` VALUES (5, 5, 1, 1, 1, 1, 1, 1, 1, 1, '2025-12-09 15:29:50');
INSERT INTO `user_permission` VALUES (6, 6, 1, 1, 1, 1, 1, 1, 1, 1, '2025-12-09 15:29:50');

-- ----------------------------
-- Table structure for user_setting
-- ----------------------------
DROP TABLE IF EXISTS `user_setting`;
CREATE TABLE `user_setting`  (
  `setting_id` int NOT NULL AUTO_INCREMENT COMMENT '设置ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `theme` tinyint(1) NULL DEFAULT 0 COMMENT '主题：0light, 1dark',
  `notification_enabled` tinyint(1) NULL DEFAULT 1 COMMENT '接收通知',
  `private_message_enabled` tinyint(1) NULL DEFAULT 1 COMMENT '接收私信',
  `language` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'zh-CN' COMMENT '语言',
  `privacy_level` enum('公开','好友可见','仅自己') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '公开' COMMENT '隐私级别',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`setting_id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `user_setting_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户设置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_setting
-- ----------------------------
INSERT INTO `user_setting` VALUES (2, 2, 0, 1, 1, 'zh-CN', '公开', '2025-12-09 15:29:50', '2025-12-09 15:29:50');
INSERT INTO `user_setting` VALUES (3, 3, 0, 1, 1, 'zh-CN', '公开', '2025-12-09 15:29:50', '2025-12-09 15:29:50');
INSERT INTO `user_setting` VALUES (4, 4, 0, 1, 1, 'zh-CN', '公开', '2025-12-09 15:29:50', '2025-12-09 15:29:50');
INSERT INTO `user_setting` VALUES (5, 5, 0, 1, 1, 'zh-CN', '公开', '2025-12-09 15:29:50', '2025-12-09 15:29:50');
INSERT INTO `user_setting` VALUES (6, 6, 0, 1, 1, 'zh-CN', '公开', '2025-12-09 15:29:50', '2025-12-09 15:29:50');
INSERT INTO `user_setting` VALUES (7, 1, 0, 1, 1, 'zh-CN', '公开', '2025-12-09 15:29:50', '2025-12-09 15:29:50');

-- ----------------------------
-- Triggers structure for table user
-- ----------------------------
DROP TRIGGER IF EXISTS `after_user_insert`;
delimiter ;;
CREATE TRIGGER `after_user_insert` AFTER INSERT ON `user` FOR EACH ROW BEGIN
    
    INSERT IGNORE INTO `user_permission` (user_id) VALUES (NEW.user_id);
    
    INSERT IGNORE INTO `user_setting` (user_id) VALUES (NEW.user_id);
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
