/*
 Navicat Premium Dump SQL

 Source Server         : lnforum_localhost
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : lnforum_localhost

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 26/12/2025 20:47:52
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
INSERT INTO `category` VALUES (5, '招聘', '校园招聘和兼职信息', 5, 0, '2025-12-09 14:32:58');
INSERT INTO `category` VALUES (6, '部门动态', '学校部门通知和公告', 6, 0, '2025-12-09 14:32:58');

-- ----------------------------
-- Table structure for category_tag
-- ----------------------------
DROP TABLE IF EXISTS `category_tag`;
CREATE TABLE `category_tag`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `category_id` int NOT NULL COMMENT '分类ID',
  `tag_id` int NOT NULL COMMENT '标签ID',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_category_tag`(`category_id` ASC, `tag_id` ASC) USING BTREE,
  INDEX `idx_category`(`category_id` ASC) USING BTREE,
  INDEX `idx_tag`(`tag_id` ASC) USING BTREE,
  INDEX `idx_sort`(`sort_order` ASC) USING BTREE,
  CONSTRAINT `category_tag_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `category_tag_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`tag_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '分类-标签关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of category_tag
-- ----------------------------
INSERT INTO `category_tag` VALUES (1, 1, 1, 1, '2025-12-09 14:32:58');
INSERT INTO `category_tag` VALUES (2, 1, 2, 2, '2025-12-09 14:32:58');
INSERT INTO `category_tag` VALUES (3, 1, 6, 3, '2025-12-09 14:32:58');
INSERT INTO `category_tag` VALUES (4, 1, 7, 4, '2025-12-09 14:32:58');
INSERT INTO `category_tag` VALUES (5, 1, 8, 5, '2025-12-09 14:32:58');
INSERT INTO `category_tag` VALUES (7, 3, 3, 1, '2025-12-09 14:32:58');
INSERT INTO `category_tag` VALUES (10, 3, 5, 2, '2025-12-09 14:32:58');
INSERT INTO `category_tag` VALUES (11, 1, 20, 10, '2025-12-25 10:43:25');
INSERT INTO `category_tag` VALUES (12, 1, 12, 2, '2025-12-25 10:43:25');
INSERT INTO `category_tag` VALUES (13, 1, 15, 5, '2025-12-25 10:43:25');
INSERT INTO `category_tag` VALUES (14, 1, 19, 9, '2025-12-25 10:43:25');
INSERT INTO `category_tag` VALUES (15, 1, 13, 3, '2025-12-25 10:43:25');
INSERT INTO `category_tag` VALUES (16, 1, 14, 4, '2025-12-25 10:43:25');
INSERT INTO `category_tag` VALUES (17, 1, 16, 6, '2025-12-25 10:43:25');
INSERT INTO `category_tag` VALUES (18, 1, 21, 11, '2025-12-25 10:43:25');
INSERT INTO `category_tag` VALUES (19, 1, 11, 1, '2025-12-25 10:43:25');
INSERT INTO `category_tag` VALUES (20, 1, 18, 8, '2025-12-25 10:43:25');
INSERT INTO `category_tag` VALUES (21, 1, 17, 7, '2025-12-25 10:43:25');
INSERT INTO `category_tag` VALUES (22, 1, 22, 12, '2025-12-25 10:43:25');
INSERT INTO `category_tag` VALUES (26, 3, 24, 2, '2025-12-25 10:43:25');
INSERT INTO `category_tag` VALUES (27, 3, 31, 9, '2025-12-25 10:43:25');
INSERT INTO `category_tag` VALUES (28, 3, 27, 5, '2025-12-25 10:43:25');
INSERT INTO `category_tag` VALUES (29, 3, 26, 4, '2025-12-25 10:43:25');
INSERT INTO `category_tag` VALUES (30, 3, 29, 7, '2025-12-25 10:43:25');
INSERT INTO `category_tag` VALUES (31, 3, 23, 1, '2025-12-25 10:43:25');
INSERT INTO `category_tag` VALUES (32, 3, 25, 3, '2025-12-25 10:43:25');
INSERT INTO `category_tag` VALUES (33, 3, 30, 8, '2025-12-25 10:43:25');
INSERT INTO `category_tag` VALUES (34, 3, 28, 6, '2025-12-25 10:43:25');
INSERT INTO `category_tag` VALUES (41, 4, 32, 1, '2025-12-25 10:43:25');
INSERT INTO `category_tag` VALUES (42, 4, 33, 2, '2025-12-25 10:43:25');

-- ----------------------------
-- Table structure for follow
-- ----------------------------
DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow`  (
  `follow_id` int NOT NULL AUTO_INCREMENT COMMENT '关注ID',
  `follower_id` int NULL DEFAULT NULL COMMENT '主动ID',
  `following_id` int NULL DEFAULT NULL COMMENT '被动ID',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '0拉黑1关注',
  PRIMARY KEY (`follow_id`) USING BTREE,
  UNIQUE INDEX `uk_follower_following`(`follower_id` ASC, `following_id` ASC) USING BTREE,
  INDEX `idx_follower`(`follower_id` ASC) USING BTREE,
  INDEX `idx_following`(`following_id` ASC) USING BTREE,
  CONSTRAINT `follow_ibfk_1` FOREIGN KEY (`follower_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `follow_ibfk_2` FOREIGN KEY (`following_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '关注/关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of follow
-- ----------------------------
INSERT INTO `follow` VALUES (1, 2, 3, 1);
INSERT INTO `follow` VALUES (2, 3, 2, 1);
INSERT INTO `follow` VALUES (3, 4, 2, 1);
INSERT INTO `follow` VALUES (4, 2, 5, 1);
INSERT INTO `follow` VALUES (5, 7, 4, 1);
INSERT INTO `follow` VALUES (6, 7, 8, 1);
INSERT INTO `follow` VALUES (7, 9, 2, 1);

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
  INDEX `idx_post_type`(`post_id` ASC, `interaction_type` ASC) USING BTREE,
  INDEX `idx_user_type`(`user_id` ASC, `interaction_type` ASC) USING BTREE,
  INDEX `idx_post_comment`(`post_id` ASC, `interaction_type` ASC, `create_time` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_interaction_composite`(`post_id` ASC, `interaction_type` ASC, `status` ASC, `create_time` DESC) USING BTREE,
  CONSTRAINT `interaction_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `interaction_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `interaction_ibfk_3` FOREIGN KEY (`parent_id`) REFERENCES `interaction` (`interaction_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 69 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '互动表' ROW_FORMAT = DYNAMIC;

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
INSERT INTO `interaction` VALUES (9, 8, 7, 'comment', '可以小刀吗？500出不出？', NULL, '正常', 0, 1, '2025-12-10 09:30:00', '2025-12-22 09:30:49');
INSERT INTO `interaction` VALUES (10, 8, 8, 'comment', '最低600，不能再低了', NULL, '正常', 0, 1, '2025-12-10 09:45:00', '2025-12-22 09:30:49');
INSERT INTO `interaction` VALUES (11, 9, 9, 'comment', '我正好在二食堂，已接单！', NULL, '正常', 0, 1, '2025-12-10 11:35:00', '2025-12-22 09:30:49');
INSERT INTO `interaction` VALUES (12, 8, 2, 'like', NULL, NULL, '正常', 0, 1, '2025-12-10 10:00:00', '2025-12-22 09:30:49');
INSERT INTO `interaction` VALUES (13, 10, 3, 'like', NULL, NULL, '正常', 0, 1, '2025-12-10 19:00:00', '2025-12-22 09:30:49');
INSERT INTO `interaction` VALUES (14, 11, 7, 'like', NULL, NULL, '正常', 0, 1, '2025-12-11 08:30:00', '2025-12-22 09:30:49');
INSERT INTO `interaction` VALUES (15, 12, 1, 'like', NULL, NULL, '正常', 0, 1, '2025-12-25 13:08:34', '2025-12-25 17:10:00');
INSERT INTO `interaction` VALUES (30, 2, 1, 'like', NULL, NULL, '正常', 0, 1, '2025-12-25 13:09:00', '2025-12-25 17:10:10');
INSERT INTO `interaction` VALUES (31, 1, 1, 'like', NULL, NULL, '正常', 0, 1, '2025-12-25 13:33:47', '2025-12-26 09:07:29');
INSERT INTO `interaction` VALUES (37, 2, 1, 'comment', '111', NULL, '正常', 0, 1, '2025-12-26 09:10:14', '2025-12-26 09:10:14');
INSERT INTO `interaction` VALUES (44, 1, 1, 'comment', '1', NULL, '正常', 0, 1, '2025-12-26 10:05:58', '2025-12-26 10:05:58');
INSERT INTO `interaction` VALUES (59, 12, 1, 'comment', '1231412', NULL, '正常', 0, 1, '2025-12-26 10:31:11', '2025-12-26 10:31:11');
INSERT INTO `interaction` VALUES (60, 12, 1, 'comment', '213', NULL, '正常', 0, 1, '2025-12-26 10:38:14', '2025-12-26 10:38:14');
INSERT INTO `interaction` VALUES (61, 2, 1, 'comment', '1', NULL, '正常', 0, 1, '2025-12-26 10:38:23', '2025-12-26 10:38:23');
INSERT INTO `interaction` VALUES (62, 1, 1, 'comment', '1123', NULL, '正常', 0, 1, '2025-12-26 10:38:27', '2025-12-26 10:38:27');
INSERT INTO `interaction` VALUES (63, 2, 1, 'favorite', NULL, NULL, '正常', 0, 1, '2025-12-26 10:38:44', '2025-12-26 10:38:44');
INSERT INTO `interaction` VALUES (64, 12, 1, 'comment', '1', NULL, '正常', 0, 1, '2025-12-26 12:18:05', '2025-12-26 12:18:05');
INSERT INTO `interaction` VALUES (65, 13, 1, 'comment', '1', NULL, '正常', 0, 1, '2025-12-26 13:07:07', '2025-12-26 13:07:07');
INSERT INTO `interaction` VALUES (66, 13, 1, 'like', NULL, NULL, '正常', 0, 0, '2025-12-26 13:07:16', '2025-12-26 19:27:15');
INSERT INTO `interaction` VALUES (67, 13, 1, 'comment', '1', NULL, '正常', 0, 1, '2025-12-26 19:28:01', '2025-12-26 19:28:01');
INSERT INTO `interaction` VALUES (68, 13, 1, 'favorite', NULL, NULL, '正常', 0, 1, '2025-12-26 19:29:30', '2025-12-26 19:29:40');

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `message_id` int NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` int NULL DEFAULT NULL COMMENT '发送者ID（NULL表示系统消息）',
  `receiver_id` int NULL DEFAULT NULL COMMENT '接收者ID（系统通知时可为NULL，通过message_receiver表管理）',
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
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES (1, 3, 2, 0, NULL, '1', 0, NULL, '用户', 1, '2025-12-26 15:19:06', '2025-12-09 12:20:00');
INSERT INTO `message` VALUES (2, 3, 2, 0, NULL, '最低70，已经是最低价了', 0, NULL, '用户', 1, '2025-12-26 19:57:13', '2025-12-09 12:25:00');
INSERT INTO `message` VALUES (3, NULL, NULL, 1, '欢迎加入校园社区', '欢迎张小明加入我们的校园社区，请遵守社区规则', 0, NULL, '用户', 0, '2025-12-09 15:05:00', '2025-12-09 15:00:00');
INSERT INTO `message` VALUES (4, NULL, NULL, 1, '您的商品有人咨询', '用户张小明咨询了您的考研资料，请及时回复', 0, NULL, '帖子', 0, NULL, '2025-12-09 12:20:00');
INSERT INTO `message` VALUES (5, 9, 7, 0, NULL, '同学，饭放楼下宿管桌子上了，记得趁热吃。', 0, NULL, NULL, 0, NULL, '2025-12-10 12:05:00');
INSERT INTO `message` VALUES (6, 7, 8, 0, NULL, '我想在这个周末看琴，方便吗？', 0, NULL, NULL, 1, NULL, '2025-12-10 13:00:00');
INSERT INTO `message` VALUES (7, NULL, NULL, 1, NULL, '您的跑腿订单已完成，请确认收货。', 0, NULL, NULL, 0, NULL, '2025-12-10 12:06:00');
INSERT INTO `message` VALUES (8, NULL, NULL, 1, '元旦活动通知', '2026年元旦校园社区将举办线下联谊活动，欢迎所有用户参与！', 1, 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/20251224133335_651dba1f.png', NULL, NULL, NULL, '2025-12-23 14:26:27');
INSERT INTO `message` VALUES (9, 1, 7, 0, NULL, '已封禁用户1天', 0, NULL, '举报', 0, NULL, '2025-12-23 08:35:17');
INSERT INTO `message` VALUES (10, 1, 6, 0, NULL, '未违规', 0, NULL, '举报', 0, NULL, '2025-12-23 08:35:30');
INSERT INTO `message` VALUES (11, 1, 5, 0, NULL, '您的举报已处理（结果：已通过并采取相应措施）。反馈：已封禁用户1天', 0, NULL, '举报', 0, NULL, '2025-12-23 08:46:36');
INSERT INTO `message` VALUES (12, 1, 6, 0, NULL, '', 1, 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/20251224133208_5bcdf8eb.png', '用户', 0, NULL, '2025-12-24 13:32:17');
INSERT INTO `message` VALUES (13, 1, 2, 0, NULL, '', 1, 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/20251224134918_2d7f6a38.png', '用户', 1, '2025-12-26 17:35:03', '2025-12-24 13:49:21');
INSERT INTO `message` VALUES (14, 2, 10, 0, NULL, '111', 0, NULL, '用户', 0, NULL, '2025-12-26 15:18:20');
INSERT INTO `message` VALUES (15, 2, 1, 0, NULL, '1', 0, NULL, '用户', 0, NULL, '2025-12-26 19:58:03');

-- ----------------------------
-- Table structure for message_receiver
-- ----------------------------
DROP TABLE IF EXISTS `message_receiver`;
CREATE TABLE `message_receiver`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `message_id` int NOT NULL COMMENT '消息ID',
  `receiver_id` int NOT NULL COMMENT '接收者ID',
  `is_read` tinyint(1) NULL DEFAULT 0 COMMENT '是否已读：0未读，1已读',
  `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_message_receiver`(`message_id` ASC, `receiver_id` ASC) USING BTREE COMMENT '同一消息同一接收者唯一',
  INDEX `idx_receiver_read`(`receiver_id` ASC, `is_read` ASC) USING BTREE,
  INDEX `idx_message_id`(`message_id` ASC) USING BTREE,
  CONSTRAINT `fk_mr_message` FOREIGN KEY (`message_id`) REFERENCES `message` (`message_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_mr_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息接收者关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message_receiver
-- ----------------------------
INSERT INTO `message_receiver` VALUES (1, 3, 2, 1, '2025-12-09 15:05:00', '2025-12-09 15:00:00');
INSERT INTO `message_receiver` VALUES (2, 4, 3, 0, NULL, '2025-12-09 12:20:00');
INSERT INTO `message_receiver` VALUES (3, 7, 7, 0, NULL, '2025-12-10 12:06:00');
INSERT INTO `message_receiver` VALUES (4, 5, 2, 0, NULL, '2025-12-23 14:26:27');
INSERT INTO `message_receiver` VALUES (5, 5, 3, 0, NULL, '2025-12-23 14:26:27');
INSERT INTO `message_receiver` VALUES (6, 5, 4, 0, NULL, '2025-12-23 14:26:27');

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
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态：0待审核, 1已通过, 2已删除, 3已结束，4未通过，5进行中',
  `view_count` int NULL DEFAULT 0 COMMENT '查看次数',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
  `favorite_count` int NULL DEFAULT 0 COMMENT '收藏数',
  `trending_level` tinyint(1) NULL DEFAULT 0 COMMENT '热度：0普通, 1热门',
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
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '动态表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of post
-- ----------------------------
INSERT INTO `post` VALUES (1, 2, 1, '今天图书馆人真多', '期末复习季，图书馆一座难求，大家加油！', NULL, NULL, NULL, NULL, NULL, NULL, 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/20251224133208_5bcdf8eb.png', NULL, NULL, 1, 173, 24, 9, 5, 0, '2025-12-09 09:30:00', '2025-12-09 09:30:00', '2025-12-26 14:53:45');
INSERT INTO `post` VALUES (2, 3, 1, '考研倒计时30天', '最后一个月冲刺，研友们一起加油！', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/20251224133208_5bcdf8eb.png', NULL, 1, 346, 46, 3, 19, 1, '2025-12-09 10:15:00', '2025-12-09 10:15:00', '2025-12-26 19:30:57');
INSERT INTO `post` VALUES (3, 2, 2, '代取快递', '下午3-5点有时间，可代取中通快递，小件3元，大件5元', '微信: zhxm123', '2025-12-09 17:00:00', 5.00, NULL, '菜鸟驿站', '宿舍7号楼', NULL, NULL, 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/20251224133208_5bcdf8eb.png', 3, 78, 5, 3, 2, 0, '2025-12-09 11:20:00', '2025-12-09 11:20:00', '2025-12-25 14:07:02');
INSERT INTO `post` VALUES (4, 3, 3, '出售考研资料', '2025年考研数学一全套资料，几乎全新', '电话: 13800138002', '2025-12-15 00:00:00', 80.00, '包含真题、模拟题、笔记', NULL, NULL, 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/20251224133208_5bcdf8eb.png', 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/20251224133208_5bcdf8eb.png', NULL, 1, 136, 12, 6, 7, 0, '2025-12-09 12:00:00', '2025-12-09 12:00:00', '2025-12-25 17:09:19');
INSERT INTO `post` VALUES (5, 4, 4, '捡到一卡通', '在二食堂门口捡到一张学生卡，姓名:李华', '电话: 13800138003', NULL, NULL, '蓝色卡套，信息工程学院', NULL, NULL, 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/20251224133208_5bcdf8eb.png', 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/20251224133208_5bcdf8eb.png', NULL, 1, 52, 3, 2, 1, 0, '2025-12-09 13:10:00', '2025-12-09 13:10:00', '2025-12-26 20:27:49');
INSERT INTO `post` VALUES (6, 5, 5, '招聘研究助理', '计算机学院实验室招聘研究助理2名，要求熟悉Python', '邮箱: wang@university.edu', '2025-12-20 00:00:00', NULL, '每周工作10-15小时，有科研经验者优先', NULL, NULL, NULL, 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/20251224133208_5bcdf8eb.png', NULL, 1, 210, 18, 9, 15, 1, '2025-12-09 14:00:00', '2025-12-09 14:00:00', '2025-12-24 13:35:35');
INSERT INTO `post` VALUES (7, 1, 6, '校园网络维护通知', '12月10日00:00-06:00校园网络维护，期间可能无法访问', NULL, '2025-12-10 06:00:00', NULL, NULL, NULL, NULL, NULL, 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/20251224133208_5bcdf8eb.png', NULL, 1, 320, 28, 5, 12, 0, '2025-12-09 14:30:00', '2025-12-09 14:30:00', '2025-12-24 13:35:36');
INSERT INTO `post` VALUES (8, 8, 3, '【出】雅马哈吉他，九九新', '大一买的，没怎么弹过，送琴包和变调夹。', '微信: guitar_hero', NULL, 650.00, '型号F310，原木色', NULL, NULL, 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/20251224133208_5bcdf8eb.png', NULL, 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/20251224133208_5bcdf8eb.png', 1, 15, 0, 0, 0, 0, '2025-12-23 09:16:37', '2025-12-10 09:00:00', '2025-12-26 19:42:38');
INSERT INTO `post` VALUES (9, 7, 2, '求带二食堂黄焖鸡米饭', '有些感冒不想下楼，求同学帮忙带一份大份微辣的。', '手机同号', NULL, 3.00, NULL, '二食堂', '南区宿舍3号楼', NULL, 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/20251224133208_5bcdf8eb.png', NULL, 1, 0, 0, 0, 0, 0, '2025-12-23 09:16:53', '2025-12-10 11:30:00', '2025-12-24 13:35:38');
INSERT INTO `post` VALUES (10, 2, 4, '操场捡到一个AirPods左耳', '在操场主席台附近的草坪上捡到的，请失主带另一只来配对。', NULL, NULL, NULL, 'AirPods Pro 一代', NULL, NULL, 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/20251224133208_5bcdf8eb.png', NULL, 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/20251224133208_5bcdf8eb.png', 4, 0, 0, 0, 0, 0, '2025-12-23 13:21:57', '2025-12-10 18:20:00', '2025-12-24 13:40:09');
INSERT INTO `post` VALUES (11, 3, 1, '最近天气太冷了', '大家去图书馆记得多穿点，暖气好像不太足。', NULL, NULL, NULL, NULL, NULL, NULL, 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/20251224133208_5bcdf8eb.png', NULL, NULL, 4, 0, 0, 0, 0, 0, '2025-12-23 13:21:56', '2025-12-11 08:15:00', '2025-12-24 13:35:40');
INSERT INTO `post` VALUES (12, 5, 1, '教职工公寓短租', '寒假期间短租，适合留校考研的同学，水电全免。', '电话: 138xxx', NULL, 1500.00, '一室一厅，有厨房', NULL, NULL, NULL, 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/20251224133208_5bcdf8eb.png', NULL, 1, 87, 1, 6, 0, 0, '2025-12-23 13:21:53', '2025-12-11 10:00:00', '2025-12-26 17:58:01');
INSERT INTO `post` VALUES (13, 1, 1, '1', '111', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 24, 0, 2, 1, 0, NULL, '2025-12-26 12:36:42', '2025-12-26 19:31:01');
INSERT INTO `post` VALUES (14, 1, 2, '1', '1', NULL, NULL, 1.00, '1213', NULL, NULL, NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, NULL, '2025-12-26 13:07:47', '2025-12-26 13:07:47');
INSERT INTO `post` VALUES (15, 1, 2, '789', '777', NULL, NULL, 7557.00, '777', '1', '7', NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, NULL, '2025-12-26 13:24:39', '2025-12-26 13:24:39');

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
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '动态-标签关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of post_tag
-- ----------------------------
INSERT INTO `post_tag` VALUES (1, 1, 2);
INSERT INTO `post_tag` VALUES (2, 2, 1);
INSERT INTO `post_tag` VALUES (3, 2, 2);
INSERT INTO `post_tag` VALUES (4, 4, 1);
INSERT INTO `post_tag` VALUES (5, 4, 3);
INSERT INTO `post_tag` VALUES (13, 5, 33);
INSERT INTO `post_tag` VALUES (8, 8, 3);
INSERT INTO `post_tag` VALUES (9, 9, 6);
INSERT INTO `post_tag` VALUES (10, 10, 33);
INSERT INTO `post_tag` VALUES (12, 12, 2);
INSERT INTO `post_tag` VALUES (11, 12, 5);
INSERT INTO `post_tag` VALUES (14, 13, 11);

-- ----------------------------
-- Table structure for report
-- ----------------------------
DROP TABLE IF EXISTS `report`;
CREATE TABLE `report`  (
  `report_id` int NOT NULL AUTO_INCREMENT COMMENT '举报ID',
  `reporter_id` int NOT NULL COMMENT '举报人ID',
  `target_type` enum('用户','帖子','评论') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '举报目标类型',
  `interaction` int NULL DEFAULT NULL COMMENT '被举报评论的id',
  `post_id` int NULL DEFAULT NULL COMMENT '被举报动态的id',
  `target_id` int NULL DEFAULT NULL COMMENT '举报目标ID',
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
  INDEX `report_ibfk_3`(`post_id` ASC) USING BTREE,
  INDEX `report_ibfk_4`(`interaction` ASC) USING BTREE,
  CONSTRAINT `report_ibfk_1` FOREIGN KEY (`reporter_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `report_ibfk_2` FOREIGN KEY (`admin_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `report_ibfk_3` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `report_ibfk_4` FOREIGN KEY (`interaction`) REFERENCES `interaction` (`interaction_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '举报表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of report
-- ----------------------------
INSERT INTO `report` VALUES (1, 2, '帖子', NULL, 1, NULL, '其他', '这个帖子描述的情况不实，图书馆今天人很少', NULL, 1, 1, 0, '经核实，帖子内容属实', '2025-12-09 11:00:00', '2025-12-09 14:00:00');
INSERT INTO `report` VALUES (2, 3, '评论', 10, NULL, NULL, '其他', '这个回复带有攻击性', NULL, 0, NULL, NULL, NULL, '2025-12-09 11:30:00', NULL);
INSERT INTO `report` VALUES (3, 2, '帖子', NULL, 6, NULL, '垃圾信息', '这人一直在发兼职刷单广告', NULL, 0, NULL, NULL, NULL, '2025-12-11 15:00:00', NULL);
INSERT INTO `report` VALUES (4, 2, '用户', NULL, NULL, 3, '垃圾信息', '一直发垃圾信息', 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/20251224133208_5bcdf8eb.png', 1, 1, 1, '已封禁用户1天', '2025-12-22 15:24:40', '2025-12-23 02:53:36');
INSERT INTO `report` VALUES (5, 4, '用户', NULL, NULL, 3, '违法违规', '违法', 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/20251224133208_5bcdf8eb.png', 1, 1, 1, '已封禁用户1天', '2025-12-23 12:13:25', '2025-12-23 04:15:25');
INSERT INTO `report` VALUES (6, 6, '用户', NULL, NULL, 4, '欺诈', '骗我', 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/20251224133208_5bcdf8eb.png', 1, 1, 0, '未违规', '2025-12-23 12:13:55', '2025-12-23 08:35:30');
INSERT INTO `report` VALUES (7, 7, '帖子', NULL, 8, NULL, '欺诈', '怀疑这把吉他是假货', NULL, 1, 1, 1, '已封禁用户1天', '2025-12-23 15:10:00', '2025-12-23 08:35:17');
INSERT INTO `report` VALUES (8, 8, '评论', 5, NULL, NULL, '其他', '这条评论有辱骂', NULL, 0, NULL, NULL, NULL, '2025-12-23 15:12:00', NULL);
INSERT INTO `report` VALUES (9, 9, '帖子', NULL, 10, NULL, '侵权', '帖子图片涉嫌侵权', NULL, 0, NULL, NULL, NULL, '2025-12-23 15:15:00', NULL);
INSERT INTO `report` VALUES (10, 2, '用户', NULL, NULL, 8, '欺诈', '怀疑账号出售假货', 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/20251224133208_5bcdf8eb.png', 0, NULL, NULL, NULL, '2025-12-23 15:18:00', NULL);
INSERT INTO `report` VALUES (11, 3, '评论', 11, NULL, NULL, '垃圾信息', '评论反复发广告', NULL, 0, NULL, NULL, NULL, '2025-12-23 15:20:00', NULL);
INSERT INTO `report` VALUES (12, 5, '帖子', NULL, 12, NULL, '其他', '动态内容与事实不符', NULL, 1, 1, 1, '已封禁用户1天', '2025-12-23 15:22:00', '2025-12-23 08:46:36');

-- ----------------------------
-- Table structure for system_setting
-- ----------------------------
DROP TABLE IF EXISTS `system_setting`;
CREATE TABLE `system_setting`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `site_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '小宁论坛' COMMENT '站点名称',
  `icp_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '备案号',
  `site_status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '站点状态：1开启，0维护中',
  `allow_register` tinyint(1) NOT NULL DEFAULT 1 COMMENT '开放注册：1允许，0禁止',
  `sensitive_words` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '敏感词过滤，逗号分隔',
  `max_image_size` int NOT NULL DEFAULT 5 COMMENT '最大图片大小(MB)',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统全站配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_setting
-- ----------------------------
INSERT INTO `system_setting` VALUES (1, '小宁论坛', '京ICP备xxxxxx号', 1, 1, 'admin,root,system', 5, '2025-12-22 14:25:41');

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
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '标签表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag` VALUES (1, '考研', '2025-12-09 14:32:58');
INSERT INTO `tag` VALUES (2, '学习', '2025-12-09 14:32:58');
INSERT INTO `tag` VALUES (3, '二手', '2025-12-09 14:32:58');
INSERT INTO `tag` VALUES (5, '租房', '2025-12-09 14:32:58');
INSERT INTO `tag` VALUES (6, '美食', '2025-12-09 14:32:58');
INSERT INTO `tag` VALUES (7, '运动', '2025-12-09 14:32:58');
INSERT INTO `tag` VALUES (8, '社团', '2025-12-09 14:32:58');
INSERT INTO `tag` VALUES (9, '快递', '2025-12-09 14:32:58');
INSERT INTO `tag` VALUES (11, '校园日常', '2025-12-25 10:43:25');
INSERT INTO `tag` VALUES (12, '学习搭子', '2025-12-25 10:43:25');
INSERT INTO `tag` VALUES (13, '期末冲刺', '2025-12-25 10:43:25');
INSERT INTO `tag` VALUES (14, '校园干饭指南', '2025-12-25 10:43:25');
INSERT INTO `tag` VALUES (15, '宿舍日常', '2025-12-25 10:43:25');
INSERT INTO `tag` VALUES (16, '校园拍照', '2025-12-25 10:43:25');
INSERT INTO `tag` VALUES (17, '社团招新', '2025-12-25 10:43:25');
INSERT INTO `tag` VALUES (18, '校园求助', '2025-12-25 10:43:25');
INSERT INTO `tag` VALUES (19, '找搭子', '2025-12-25 10:43:25');
INSERT INTO `tag` VALUES (20, '图书馆日常', '2025-12-25 10:43:25');
INSERT INTO `tag` VALUES (21, '校园散步', '2025-12-25 10:43:25');
INSERT INTO `tag` VALUES (22, '院系活动', '2025-12-25 10:43:25');
INSERT INTO `tag` VALUES (23, '校园二手', '2025-12-25 10:43:25');
INSERT INTO `tag` VALUES (24, '二手教材', '2025-12-25 10:43:25');
INSERT INTO `tag` VALUES (25, '毕业甩卖', '2025-12-25 10:43:25');
INSERT INTO `tag` VALUES (26, '数码闲置', '2025-12-25 10:43:25');
INSERT INTO `tag` VALUES (27, '宿舍神器', '2025-12-25 10:43:25');
INSERT INTO `tag` VALUES (28, '考研资料', '2025-12-25 10:43:25');
INSERT INTO `tag` VALUES (29, '服饰鞋包', '2025-12-25 10:43:25');
INSERT INTO `tag` VALUES (30, '求购', '2025-12-25 10:43:25');
INSERT INTO `tag` VALUES (31, '低价出物', '2025-12-25 10:43:25');
INSERT INTO `tag` VALUES (32, '失物', '2025-12-25 10:43:25');
INSERT INTO `tag` VALUES (33, '招领', '2025-12-25 10:43:25');

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
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '交易任务表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of trade_task
-- ----------------------------
INSERT INTO `trade_task` VALUES (1, 'TR202512090001', 4, 3, 2, '二手交易', '购买考研资料', '2025年考研数学一全套资料', 70.00, NULL, NULL, '包含真题、模拟题、笔记', NULL, '李小红', '13800138002', 1, '2025-12-09 13:00:00', '2025-12-09 13:30:00', NULL, NULL);
INSERT INTO `trade_task` VALUES (2, 'ER202512090001', 3, 2, 4, '跑腿服务', '代取快递', '代取中通快递包裹', 5.00, '菜鸟驿站', '宿舍7号楼', '小件包裹', 20, '张小明', '13800138001', 0, '2025-12-09 13:30:00', '2025-12-09 13:30:00', NULL, NULL);
INSERT INTO `trade_task` VALUES (3, 'RN202512100088', 9, 7, 9, '跑腿服务', '求带二食堂黄焖鸡米饭', '微辣，大份', 3.00, '二食堂', '南区宿舍3号楼', NULL, NULL, NULL, NULL, 1, '2025-12-10 11:35:00', '2025-12-10 12:10:00', NULL, NULL);
INSERT INTO `trade_task` VALUES (4, 'TR202512100099', 8, 8, 7, '二手交易', '购买雅马哈吉他', NULL, 600.00, NULL, NULL, NULL, NULL, NULL, NULL, 0, '2025-12-10 14:00:00', '2025-12-22 09:30:50', NULL, NULL);

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
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', 'Admin User', 'admin123', '13800138084', NULL, 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/2025122400001.jpg', 0, NULL, NULL, 0, 0, 0, '2025-12-09 14:32:58', NULL);
INSERT INTO `user` VALUES (2, '张小明', '张小明', '$2a$10$abc123', '13259545595', 'zhxm@example.com', 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/2025122400002.jpg', 2, '热爱学习的程序员', '北京市海淀区', 3, 0, 0, '2025-12-09 15:00:00', '2025-12-09 18:30:00');
INSERT INTO `user` VALUES (3, '李小红', '李小红', '$2a$10$def456', '13800138002', 'lixh@example.com', 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/2025122400003.jpg', 2, '考研进行中', '北京市朝阳区', 3, 1, 0, '2025-12-09 15:05:00', '2025-12-09 19:20:00');
INSERT INTO `user` VALUES (4, '跑腿小王', '王五', '$2a$10$ghi789', '13800138003', 'paitw@example.com', 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/2025122400004.jpg', 1, '专业跑腿，安全快捷', '北京市丰台区', 2, 0, 0, '2025-12-09 15:10:00', '2025-12-09 20:15:00');
INSERT INTO `user` VALUES (5, '王老师', '王建国', '$2a$10$jkl012', '13800138004', 'wangls@example.com', 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/2025122400005.jpg', 1, '计算机系教师', '北京市西城区', 3, 0, 1, '2025-12-09 15:15:00', '2025-12-09 17:45:00');
INSERT INTO `user` VALUES (6, '校园记者', '赵六', '$2a$10$mno345', '13800138005', 'xyjz@example.com', 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/2025122400006.jpg', 2, '校园新闻第一时间', '北京市东城区', 3, 0, 1, '2025-12-09 15:20:00', '2025-12-09 16:30:00');
INSERT INTO `user` VALUES (7, 'freshman_li', '李华', '$2a$10$testpwd7', '13900000007', 'lihua@uni.edu', 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/2025122400007.jpg', 1, '大一新生，请多关照', '南区宿舍3号楼', 3, 0, 0, '2025-12-22 09:30:49', NULL);
INSERT INTO `user` VALUES (8, 'music_fan', '陈悦', '$2a$10$testpwd8', '13900000008', 'chenyue@uni.edu', 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/2025122400008.jpg', 2, '吉他社副社长 | 寻找乐队伙伴', '北区宿舍A栋', 3, 0, 0, '2025-12-22 09:30:49', NULL);
INSERT INTO `user` VALUES (9, 'runner_fast', '赵速', '$2a$10$testpwd9', '13900000009', 'zhaosu@uni.edu', 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/2025122400009.jpg', 1, '全校接单，使命必达', '东区男生宿舍6栋', 2, 0, 0, '2025-12-22 09:30:49', NULL);
INSERT INTO `user` VALUES (10, 'tech_admin', '系统管理员', '$2a$10$testpwd10', '13900000010', 'admin2@uni.edu', 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/2025122400010.jpg', 1, '维护社区秩序', '行政楼202', 1, 0, 0, '2025-12-22 09:30:49', NULL);
INSERT INTO `user` VALUES (11, 'wang', '王', '123456', '123123413241', '12341232@qq.com', 'https://inforum.oss-cn-wulanchabu.aliyuncs.com/images/20251224/2025122400010.jpg', 1, '大学生', '宁夏大学', 3, 0, 0, '2025-12-24 03:38:34', NULL);

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
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户封禁历史表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_ban_history
-- ----------------------------
INSERT INTO `user_ban_history` VALUES (1, 6, 1, '封禁', '{\"can_buy\": 1, \"can_like\": 1, \"can_post\": 1, \"can_sell\": 1, \"can_follow\": 1, \"can_comment\": 1, \"can_message\": 1, \"can_run_errand\": 1}', '{\"can_buy\": 1, \"can_like\": 1, \"can_post\": 0, \"can_sell\": 1, \"can_follow\": 1, \"can_comment\": 1, \"can_message\": 1, \"can_run_errand\": 1}', '发布虚假广告信息', 7, '2025-12-09 15:30:00', '2025-12-16 15:30:00', 0, '2025-12-09 15:30:00');
INSERT INTO `user_ban_history` VALUES (2, 3, 1, '封禁', '{\"can_buy\": 1, \"can_like\": 1, \"can_post\": 1, \"can_sell\": 1, \"can_follow\": 1, \"can_comment\": 1, \"can_message\": 1, \"can_run_errand\": 1}', '{\"can_buy\": 0, \"can_like\": 0, \"can_post\": 0, \"can_sell\": 0, \"can_follow\": 0, \"can_comment\": 0, \"can_message\": 0, \"can_run_errand\": 0}', '举报原因：垃圾信息 - 一直发垃圾信息', 1, '2025-12-23 02:53:36', '2025-12-24 02:53:36', 0, '2025-12-23 02:53:36');
INSERT INTO `user_ban_history` VALUES (3, 3, 1, '封禁', '{\"can_buy\": 1, \"can_like\": 1, \"can_post\": 1, \"can_sell\": 1, \"can_follow\": 1, \"can_comment\": 1, \"can_message\": 1, \"can_run_errand\": 1}', '{\"can_buy\": 0, \"can_like\": 0, \"can_post\": 0, \"can_sell\": 0, \"can_follow\": 0, \"can_comment\": 0, \"can_message\": 0, \"can_run_errand\": 0}', '举报原因：违法违规 - 违法', 1, '2025-12-23 04:15:25', '2025-12-24 04:15:25', 1, '2025-12-23 04:15:25');
INSERT INTO `user_ban_history` VALUES (4, 8, 1, '封禁', '{\"can_buy\": 1, \"can_like\": 1, \"can_post\": 1, \"can_sell\": 1, \"can_follow\": 1, \"can_comment\": 1, \"can_message\": 1, \"can_run_errand\": 1}', '{\"can_buy\": 1, \"can_like\": 1, \"can_post\": 0, \"can_sell\": 1, \"can_follow\": 1, \"can_comment\": 1, \"can_message\": 1, \"can_run_errand\": 1}', '举报原因：欺诈 - 怀疑这把吉他是假货', 1, '2025-12-23 08:35:17', '2025-12-24 08:35:17', 1, '2025-12-23 08:35:17');
INSERT INTO `user_ban_history` VALUES (5, 5, 1, '封禁', '{\"can_buy\": 1, \"can_like\": 1, \"can_post\": 1, \"can_sell\": 1, \"can_follow\": 1, \"can_comment\": 1, \"can_message\": 1, \"can_run_errand\": 1}', '{\"can_buy\": 1, \"can_like\": 1, \"can_post\": 0, \"can_sell\": 1, \"can_follow\": 1, \"can_comment\": 1, \"can_message\": 1, \"can_run_errand\": 1}', '举报原因：其他 - 动态内容与事实不符', 1, '2025-12-23 08:46:36', '2025-12-24 08:46:36', 1, '2025-12-23 08:46:36');
INSERT INTO `user_ban_history` VALUES (6, 1, 1, '封禁', '{\"canBuy\": 1, \"userId\": 1, \"canLike\": 1, \"canPost\": 1, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1765294190000, \"canRunErrand\": 1, \"permissionId\": 1}', '{\"canBuy\": 1, \"userId\": 1, \"canLike\": 1, \"canPost\": 0, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1766572989000, \"canRunErrand\": 1, \"permissionId\": 1}', '违规操作', 1, '2025-12-24 02:43:10', '2025-12-25 02:43:10', 1, '2025-12-24 02:43:10');
INSERT INTO `user_ban_history` VALUES (7, 2, 1, '封禁', '{\"canBuy\": 1, \"userId\": 2, \"canLike\": 1, \"canPost\": 1, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1765294190000, \"canRunErrand\": 1, \"permissionId\": 2}', '{\"canBuy\": 1, \"userId\": 2, \"canLike\": 1, \"canPost\": 0, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1766573045000, \"canRunErrand\": 1, \"permissionId\": 2}', '违规操作', 1, '2025-12-24 02:44:06', '2025-12-25 02:44:06', 0, '2025-12-24 02:44:06');
INSERT INTO `user_ban_history` VALUES (8, 2, 1, '封禁', '{\"canBuy\": 1, \"userId\": 2, \"canLike\": 1, \"canPost\": 0, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1766573045000, \"canRunErrand\": 1, \"permissionId\": 2}', '{\"canBuy\": 1, \"userId\": 2, \"canLike\": 1, \"canPost\": 0, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1766573045000, \"canRunErrand\": 1, \"permissionId\": 2}', '违规操作', 3, '2025-12-24 02:44:18', '2025-12-27 02:44:18', 0, '2025-12-24 02:44:18');
INSERT INTO `user_ban_history` VALUES (9, 2, 1, '封禁', '{\"canBuy\": 1, \"userId\": 2, \"canLike\": 1, \"canPost\": 0, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1766573045000, \"canRunErrand\": 1, \"permissionId\": 2}', '{\"canBuy\": 1, \"userId\": 2, \"canLike\": 1, \"canPost\": 0, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1766573045000, \"canRunErrand\": 1, \"permissionId\": 2}', '违规操作', 3, '2025-12-24 02:57:43', '2025-12-27 02:57:43', 0, '2025-12-24 02:57:43');
INSERT INTO `user_ban_history` VALUES (10, 2, 1, '封禁', '{\"canBuy\": 1, \"userId\": 2, \"canLike\": 1, \"canPost\": 1, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1766574423000, \"canRunErrand\": 1, \"permissionId\": 2}', '{\"canBuy\": 1, \"userId\": 2, \"canLike\": 1, \"canPost\": 0, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1766574428000, \"canRunErrand\": 1, \"permissionId\": 2}', '违规操作', 1, '2025-12-24 03:07:08', '2025-12-25 03:07:08', 0, '2025-12-24 03:07:08');
INSERT INTO `user_ban_history` VALUES (11, 2, 1, '封禁', '{\"canBuy\": 1, \"userId\": 2, \"canLike\": 1, \"canPost\": 0, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1766574428000, \"canRunErrand\": 1, \"permissionId\": 2}', '{\"canBuy\": 1, \"userId\": 2, \"canLike\": 1, \"canPost\": 0, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1766574428000, \"canRunErrand\": 1, \"permissionId\": 2}', '违规操作', 3, '2025-12-24 03:07:15', '2025-12-27 03:07:15', 0, '2025-12-24 03:07:15');
INSERT INTO `user_ban_history` VALUES (12, 4, 1, '封禁', '{\"canBuy\": 1, \"userId\": 4, \"canLike\": 1, \"canPost\": 1, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1765294190000, \"canRunErrand\": 1, \"permissionId\": 4}', '{\"canBuy\": 1, \"userId\": 4, \"canLike\": 1, \"canPost\": 0, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1766574935000, \"canRunErrand\": 1, \"permissionId\": 4}', '违规操作', 3, '2025-12-24 03:15:36', '2025-12-27 03:15:36', 1, '2025-12-24 03:15:36');
INSERT INTO `user_ban_history` VALUES (13, 6, 1, '封禁', '{\"canBuy\": 1, \"userId\": 6, \"canLike\": 1, \"canPost\": 1, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1765294190000, \"canRunErrand\": 1, \"permissionId\": 6}', '{\"canBuy\": 1, \"userId\": 6, \"canLike\": 1, \"canPost\": 0, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1766574952000, \"canRunErrand\": 1, \"permissionId\": 6}', '违规操作', 3, '2025-12-24 03:15:53', '2025-12-27 03:15:53', 1, '2025-12-24 03:15:53');
INSERT INTO `user_ban_history` VALUES (14, 2, 1, '封禁', '{\"canBuy\": 1, \"userId\": 2, \"canLike\": 1, \"canPost\": 1, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1766575088000, \"canRunErrand\": 1, \"permissionId\": 2}', '{\"canBuy\": 1, \"userId\": 2, \"canLike\": 1, \"canPost\": 0, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1766575950000, \"canRunErrand\": 1, \"permissionId\": 2}', '违规操作', 1, '2025-12-24 03:32:30', '2025-12-25 03:32:30', 0, '2025-12-24 03:32:30');
INSERT INTO `user_ban_history` VALUES (15, 2, 1, '封禁', '{\"canBuy\": 1, \"userId\": 2, \"canLike\": 1, \"canPost\": 0, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1766575950000, \"canRunErrand\": 1, \"permissionId\": 2}', '{\"canBuy\": 1, \"userId\": 2, \"canLike\": 1, \"canPost\": 0, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1766575950000, \"canRunErrand\": 1, \"permissionId\": 2}', '违规操作', 3, '2025-12-24 03:32:38', '2025-12-27 03:32:38', 0, '2025-12-24 03:32:38');
INSERT INTO `user_ban_history` VALUES (16, 2, 1, '封禁', '{\"canBuy\": 1, \"userId\": 2, \"canLike\": 1, \"canPost\": 0, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1766575950000, \"canRunErrand\": 1, \"permissionId\": 2}', '{\"canBuy\": 1, \"userId\": 2, \"canLike\": 1, \"canPost\": 0, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1766575950000, \"canRunErrand\": 1, \"permissionId\": 2}', '违规操作', 0, '2025-12-24 03:32:47', NULL, 0, '2025-12-24 03:32:47');
INSERT INTO `user_ban_history` VALUES (17, 2, 1, '封禁', '{\"canBuy\": 1, \"userId\": 2, \"canLike\": 1, \"canPost\": 1, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1766585359000, \"canRunErrand\": 1, \"permissionId\": 2}', '{\"canBuy\": 1, \"userId\": 2, \"canLike\": 1, \"canPost\": 0, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1766585586000, \"canRunErrand\": 1, \"permissionId\": 2}', '违规操作', 1, '2025-12-24 06:13:07', '2025-12-25 06:13:07', 0, '2025-12-24 06:13:07');
INSERT INTO `user_ban_history` VALUES (18, 2, 1, '封禁', '{\"canBuy\": 1, \"userId\": 2, \"canLike\": 1, \"canPost\": 1, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1766585597000, \"canRunErrand\": 1, \"permissionId\": 2}', '{\"canBuy\": 1, \"userId\": 2, \"canLike\": 1, \"canPost\": 0, \"canSell\": 1, \"canFollow\": 1, \"canComment\": 1, \"canMessage\": 1, \"updateTime\": 1766585651000, \"canRunErrand\": 1, \"permissionId\": 2}', '违规操作', 1, '2025-12-24 06:14:12', '2025-12-25 06:14:12', 1, '2025-12-24 06:14:12');

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
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_permission
-- ----------------------------
INSERT INTO `user_permission` VALUES (1, 1, 1, 1, 1, 1, 1, 1, 1, 1, '2025-12-24 10:43:55');
INSERT INTO `user_permission` VALUES (2, 2, 1, 1, 1, 1, 1, 1, 1, 1, '2025-12-24 14:14:15');
INSERT INTO `user_permission` VALUES (3, 3, 1, 1, 0, 0, 1, 0, 0, 0, '2025-12-24 11:18:18');
INSERT INTO `user_permission` VALUES (4, 4, 1, 1, 1, 1, 1, 1, 1, 1, '2025-12-24 11:18:22');
INSERT INTO `user_permission` VALUES (5, 5, 1, 1, 1, 1, 1, 1, 1, 1, '2025-12-24 11:18:25');
INSERT INTO `user_permission` VALUES (6, 6, 1, 1, 1, 1, 1, 1, 1, 1, '2025-12-24 11:18:28');
INSERT INTO `user_permission` VALUES (8, 7, 1, 1, 1, 1, 1, 1, 1, 1, '2025-12-22 09:30:49');
INSERT INTO `user_permission` VALUES (9, 8, 1, 1, 1, 1, 1, 1, 1, 1, '2025-12-24 11:18:31');
INSERT INTO `user_permission` VALUES (10, 9, 1, 1, 1, 1, 1, 1, 1, 1, '2025-12-22 09:30:49');
INSERT INTO `user_permission` VALUES (11, 10, 1, 1, 1, 1, 1, 1, 1, 1, '2025-12-22 09:30:49');
INSERT INTO `user_permission` VALUES (12, 11, 1, 1, 1, 1, 1, 1, 1, 1, '2025-12-24 11:38:34');

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
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户设置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_setting
-- ----------------------------
INSERT INTO `user_setting` VALUES (2, 2, 0, 1, 1, 'zh-CN', '公开', '2025-12-09 15:29:50', '2025-12-09 15:29:50');
INSERT INTO `user_setting` VALUES (3, 3, 0, 1, 1, 'zh-CN', '公开', '2025-12-09 15:29:50', '2025-12-09 15:29:50');
INSERT INTO `user_setting` VALUES (4, 4, 0, 1, 1, 'zh-CN', '公开', '2025-12-09 15:29:50', '2025-12-09 15:29:50');
INSERT INTO `user_setting` VALUES (5, 5, 0, 1, 1, 'zh-CN', '公开', '2025-12-09 15:29:50', '2025-12-09 15:29:50');
INSERT INTO `user_setting` VALUES (6, 6, 0, 1, 1, 'zh-CN', '公开', '2025-12-09 15:29:50', '2025-12-09 15:29:50');
INSERT INTO `user_setting` VALUES (7, 1, 0, 1, 1, 'zh-CN', '公开', '2025-12-09 15:29:50', '2025-12-09 15:29:50');
INSERT INTO `user_setting` VALUES (9, 7, 0, 1, 1, 'zh-CN', '公开', '2025-12-22 09:30:49', '2025-12-22 09:30:49');
INSERT INTO `user_setting` VALUES (10, 8, 0, 1, 1, 'zh-CN', '公开', '2025-12-22 09:30:49', '2025-12-22 09:30:49');
INSERT INTO `user_setting` VALUES (11, 9, 0, 1, 1, 'zh-CN', '公开', '2025-12-22 09:30:49', '2025-12-22 09:30:49');
INSERT INTO `user_setting` VALUES (12, 10, 0, 1, 1, 'zh-CN', '公开', '2025-12-22 09:30:49', '2025-12-22 09:30:49');
INSERT INTO `user_setting` VALUES (13, 11, 0, 1, 1, 'zh-CN', '公开', '2025-12-24 11:38:34', '2025-12-24 11:38:34');

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
