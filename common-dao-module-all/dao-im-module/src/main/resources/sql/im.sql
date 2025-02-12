/*
 Navicat Premium Data Transfer

 Source Server         : 124.70.23.222
 Source Server Type    : MySQL
 Source Server Version : 50736
 Source Host           : 124.70.23.222:3306
 Source Schema         : im

 Target Server Type    : MySQL
 Target Server Version : 50736
 File Encoding         : 65001

 Date: 19/02/2023 15:33:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for apply
-- ----------------------------
DROP TABLE IF EXISTS `apply`;
CREATE TABLE `apply`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uid` bigint(20) NULL DEFAULT NULL,
  `tid` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of apply
-- ----------------------------
INSERT INTO `apply` VALUES (4, 1, 2);
INSERT INTO `apply` VALUES (5, 1, 3);

-- ----------------------------
-- Table structure for friend
-- ----------------------------
DROP TABLE IF EXISTS `friend`;
CREATE TABLE `friend`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uid` bigint(20) NULL DEFAULT NULL,
  `fid` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of friend
-- ----------------------------
INSERT INTO `friend` VALUES (18, 2, 1);
INSERT INTO `friend` VALUES (19, 1, 2);
INSERT INTO `friend` VALUES (20, 3, 1);
INSERT INTO `friend` VALUES (21, 1, 3);

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `from` bigint(20) NULL DEFAULT NULL COMMENT '来源id',
                            `to` bigint(20) NULL DEFAULT NULL COMMENT '给谁',
                            `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型  ',
                            `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                            `time` datetime NULL DEFAULT NULL,
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
-- ----------------------------
-- Records of message
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'user1','user1', '$10$pl9yg8GotBQysL75OvXkI.an6u52mQ4kseoPQjUO3g0fjs2Ix.Fpi', 'https://cdn.uviewui.com/uview/album/4.jpg');
INSERT INTO `user` VALUES (2, 'user2','user2', '$10$pl9yg8GotBQysL75OvXkI.an6u52mQ4kseoPQjUO3g0fjs2Ix.Fpi', 'https://img2.baidu.com/it/u=260211041,3935441240&fm=253&app=120&size=w931&n=0&f=JPEG&fmt=auto?sec=1676048400&t=1f746e561a2d0cdd7abdd8567e3f620d');
INSERT INTO `user` VALUES (3, 'user3','user3', '$10$pl9yg8GotBQysL75OvXkI.an6u52mQ4kseoPQjUO3g0fjs2Ix.Fpi', 'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg');
INSERT INTO `user` VALUES (4, 'user4','user4', '$10$pl9yg8GotBQysL75OvXkI.an6u52mQ4kseoPQjUO3g0fjs2Ix.Fpi', 'https://img2.baidu.com/it/u=1035356506,3713698341&fm=253&app=138&size=w931&n=0&f=JPEG&fmt=auto?sec=1675962000&t=6ef8fe24b49bb84eca0feeae9ec678d5');

SET FOREIGN_KEY_CHECKS = 1;
