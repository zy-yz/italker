create DATABASE italker;
use italker;
/*
 Navicat Premium Data Transfer

 Source Server         : 云服务器
 Source Server Type    : MySQL
 Source Server Version : 80018
 Source Host           : 121.196.205.196:3306
 Source Schema         : italker

 Target Server Type    : MySQL
 Target Server Version : 80018
 File Encoding         : 65001

 Date: 08/01/2020 11:31:39
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_apply
-- ----------------------------
DROP TABLE IF EXISTS `TB_APPLY`;
CREATE TABLE `TB_APPLY` (
  `id` varchar(255) NOT NULL,
  `applicantId` varchar(255) DEFAULT NULL,
  `attach` text,
  `createdAt` datetime NOT NULL,
  `description` varchar(255) NOT NULL,
  `targetId` varchar(255) NOT NULL,
  `type` int(11) NOT NULL,
  `updatedAt` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9c6i8dqcsm3y1sk23xcwdjgby` (`applicantId`),
  CONSTRAINT `FK9c6i8dqcsm3y1sk23xcwdjgby` FOREIGN KEY (`applicantId`) REFERENCES `TB_USER` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_group
-- ----------------------------
DROP TABLE IF EXISTS `tb_group`;
CREATE TABLE `tb_group` (
  `id` varchar(255) NOT NULL,
  `createAt` datetime NOT NULL,
  `description` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `ownerId` varchar(255) NOT NULL,
  `picture` varchar(255) NOT NULL,
  `updateAt` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKq7tij6roe3v7vcwi235tncxv7` (`ownerId`),
  CONSTRAINT `FKq7tij6roe3v7vcwi235tncxv7` FOREIGN KEY (`ownerId`) REFERENCES `tb_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_group_member
-- ----------------------------
DROP TABLE IF EXISTS `tb_group_member`;
CREATE TABLE `tb_group_member` (
  `id` varchar(255) NOT NULL,
  `alias` varchar(255) DEFAULT NULL,
  `createAt` datetime NOT NULL,
  `groupId` varchar(255) NOT NULL,
  `notifyLevel` int(11) NOT NULL,
  `permissionType` int(11) NOT NULL,
  `updateAt` datetime NOT NULL,
  `userId` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKr95872sjqqt1duwuqequglbob` (`groupId`),
  KEY `FKi9c4bppl14q0yxi51v6pbstpl` (`userId`),
  CONSTRAINT `FKi9c4bppl14q0yxi51v6pbstpl` FOREIGN KEY (`userId`) REFERENCES `tb_user` (`id`),
  CONSTRAINT `FKr95872sjqqt1duwuqequglbob` FOREIGN KEY (`groupId`) REFERENCES `tb_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_message
-- ----------------------------
DROP TABLE IF EXISTS `tb_message`;
CREATE TABLE `tb_message` (
  `id` varchar(255) NOT NULL,
  `attach` varchar(255) DEFAULT NULL,
  `content` text NOT NULL,
  `createdAt` datetime NOT NULL,
  `groupId` varchar(255) DEFAULT NULL,
  `receiverId` varchar(255) DEFAULT NULL,
  `senderId` varchar(255) NOT NULL,
  `type` int(11) NOT NULL,
  `updatedAt` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK324lh5xrmhvukar5w5vjohjsg` (`groupId`),
  KEY `FK6w4nf1is0lo6itrm62qh6fwm9` (`receiverId`),
  KEY `FKqno27bq3qbfpjq8ptfa1hry20` (`senderId`),
  CONSTRAINT `FK324lh5xrmhvukar5w5vjohjsg` FOREIGN KEY (`groupId`) REFERENCES `tb_group` (`id`),
  CONSTRAINT `FK6w4nf1is0lo6itrm62qh6fwm9` FOREIGN KEY (`receiverId`) REFERENCES `tb_user` (`id`),
  CONSTRAINT `FKqno27bq3qbfpjq8ptfa1hry20` FOREIGN KEY (`senderId`) REFERENCES `tb_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_push_history
-- ----------------------------
DROP TABLE IF EXISTS `tb_push_history`;
CREATE TABLE `tb_push_history` (
  `id` varchar(255) NOT NULL,
  `arrivelAt` datetime DEFAULT NULL,
  `createdAt` datetime NOT NULL,
  `entity` blob NOT NULL,
  `entityType` int(11) NOT NULL,
  `receiverId` varchar(255) NOT NULL,
  `receiverPushId` varchar(255) DEFAULT NULL,
  `senderId` varchar(255) DEFAULT NULL,
  `updateAt` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKd74cyeys8vpmo4rri4fgiqsad` (`receiverId`),
  KEY `FKqwq79iikbk4uwx6377igb5t7u` (`senderId`),
  CONSTRAINT `FKd74cyeys8vpmo4rri4fgiqsad` FOREIGN KEY (`receiverId`) REFERENCES `tb_user` (`id`),
  CONSTRAINT `FKqwq79iikbk4uwx6377igb5t7u` FOREIGN KEY (`senderId`) REFERENCES `tb_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user` (
  `id` varchar(255) NOT NULL,
  `createAt` datetime NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `lastReceivedAt` datetime DEFAULT NULL,
  `name` varchar(128) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(64) NOT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `pushId` varchar(255) DEFAULT NULL,
  `sex` int(11) NOT NULL,
  `token` varchar(255) DEFAULT NULL,
  `updateAt` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_nvlr3kdy2ky121gol63otka7p` (`name`),
  UNIQUE KEY `UK_4cgso11t7xt196pe2fnmqfyxa` (`phone`),
  UNIQUE KEY `UK_6fin1quj959u8hxyits8mgv6f` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_user_follow
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_follow`;
CREATE TABLE `tb_user_follow` (
  `id` varchar(255) NOT NULL,
  `alias` varchar(255) DEFAULT NULL,
  `createAt` datetime NOT NULL,
  `originId` varchar(255) NOT NULL,
  `targetId` varchar(255) NOT NULL,
  `updateAt` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhg1xm1knhy1j9yw8xq3m2susk` (`originId`),
  KEY `FK8g0jhnfd4p3alq4dx7i7sojou` (`targetId`),
  CONSTRAINT `FK8g0jhnfd4p3alq4dx7i7sojou` FOREIGN KEY (`targetId`) REFERENCES `tb_user` (`id`),
  CONSTRAINT `FKhg1xm1knhy1j9yw8xq3m2susk` FOREIGN KEY (`originId`) REFERENCES `tb_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
