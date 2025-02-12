/*
 Navicat Premium Data Transfer

 Source Server         : 虚拟机
 Source Server Type    : MySQL
 Source Server Version : 50736
 Source Host           : 192.168.200.128:3306
 Source Schema         : im

 Target Server Type    : MySQL
 Target Server Version : 50736
 File Encoding         : 65001

 Date: 10/08/2023 15:27:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat_window
-- ----------------------------
DROP TABLE IF EXISTS `chat_window`;
CREATE TABLE `chat_window`  (
  `chat_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '聊天窗口id',
  `chat_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型 群聊|私聊',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`chat_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '聊天窗口' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
