/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 100416
 Source Host           : localhost:3306
 Source Schema         : nickdb

 Target Server Type    : MySQL
 Target Server Version : 100416
 File Encoding         : 65001

 Date: 01/06/2021 16:47:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for appointments
-- ----------------------------
DROP TABLE IF EXISTS `appointments`;
CREATE TABLE `appointments`  (
  `Appointment_ID` int NOT NULL AUTO_INCREMENT,
  `Title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Description` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Location` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Start` datetime NULL DEFAULT NULL,
  `End` datetime NULL DEFAULT NULL,
  `Create_Date` datetime NULL DEFAULT NULL,
  `Created_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Last_Update` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `Last_Updated_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Customer_ID` int NULL DEFAULT NULL,
  `User_ID` int NULL DEFAULT NULL,
  `Contact_Id` int NULL DEFAULT NULL,
  PRIMARY KEY (`Appointment_ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of appointments
-- ----------------------------
INSERT INTO `appointments` VALUES (2, 'q', 'q', 'q', 'q', '2021-05-30 23:00:00', '2021-05-31 12:00:00', NULL, '1', NULL, '1', 3, 1, 1);
INSERT INTO `appointments` VALUES (3, 'w', 'ww', 'ww', 'w', '2021-05-31 03:00:00', '2021-05-31 13:00:00', NULL, '1', '2021-06-01 16:32:02', '1', 4, 1, 1);
INSERT INTO `appointments` VALUES (4, 'e', 'e', 'e', 'e', '2021-06-03 02:00:00', '2021-06-03 05:09:00', NULL, '1', '2021-05-31 23:43:57', '1', 5, 1, 2);

-- ----------------------------
-- Table structure for contacts
-- ----------------------------
DROP TABLE IF EXISTS `contacts`;
CREATE TABLE `contacts`  (
  `Contact_ID` int NOT NULL AUTO_INCREMENT,
  `Contact_Name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`Contact_ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of contacts
-- ----------------------------
INSERT INTO `contacts` VALUES (1, 'Contact1', 'contact1@gmail.com');
INSERT INTO `contacts` VALUES (2, 'Contact2', 'contact2@hotmail.com');

-- ----------------------------
-- Table structure for countries
-- ----------------------------
DROP TABLE IF EXISTS `countries`;
CREATE TABLE `countries`  (
  `Country_ID` int NOT NULL AUTO_INCREMENT,
  `Country` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Create_Date` datetime NULL DEFAULT NULL,
  `Created_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Last_Update` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `Last_Update_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`Country_ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of countries
-- ----------------------------
INSERT INTO `countries` VALUES (1, 'Canada', '2021-05-23 20:31:21', 'test', '2021-05-23 20:31:29', 'test');
INSERT INTO `countries` VALUES (2, 'United Kingdom', '2021-05-23 20:33:10', 'test', '2021-05-23 20:33:21', 'test');
INSERT INTO `countries` VALUES (3, 'United States', '2021-05-23 20:33:38', 'test', '2021-05-23 20:33:48', 'test');

-- ----------------------------
-- Table structure for customers
-- ----------------------------
DROP TABLE IF EXISTS `customers`;
CREATE TABLE `customers`  (
  `Customer_ID` int NOT NULL AUTO_INCREMENT,
  `Customer_Name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Postal_Code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Phone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Create_Date` datetime NULL DEFAULT NULL,
  `Created_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Last_Update` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `Last_Updated_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Division_ID` int NULL DEFAULT NULL,
  PRIMARY KEY (`Customer_ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of customers
-- ----------------------------
INSERT INTO `customers` VALUES (3, 'aaa', 'bbb', 'ccc', '123', NULL, '1', NULL, '1', 36);
INSERT INTO `customers` VALUES (4, 'qqq', 'www', 'eee', '2223', NULL, '1', '2021-06-01 15:52:24', '1', 45);
INSERT INTO `customers` VALUES (5, 'zzz', 'xxx', 'dddd', '33333', NULL, '1', '2021-06-01 16:09:27', '1', 2);

-- ----------------------------
-- Table structure for first_level_divisions
-- ----------------------------
DROP TABLE IF EXISTS `first_level_divisions`;
CREATE TABLE `first_level_divisions`  (
  `Division_ID` int NOT NULL AUTO_INCREMENT,
  `Division` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Create_Date` datetime NULL DEFAULT NULL,
  `Created_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Last_Update` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `Last_Update_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Country_ID` int NULL DEFAULT NULL,
  PRIMARY KEY (`Division_ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 46 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of first_level_divisions
-- ----------------------------
INSERT INTO `first_level_divisions` VALUES (1, 'Alberta', '2021-05-23 21:52:49', 'test', '2021-05-23 21:52:56', 'test', 1);
INSERT INTO `first_level_divisions` VALUES (2, 'British Columbia', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 1);
INSERT INTO `first_level_divisions` VALUES (3, 'Manitoba', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 1);
INSERT INTO `first_level_divisions` VALUES (4, 'New Brunswick', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 1);
INSERT INTO `first_level_divisions` VALUES (5, 'Newfoundland and Labrador', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 1);
INSERT INTO `first_level_divisions` VALUES (6, 'Nova Scotia', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 1);
INSERT INTO `first_level_divisions` VALUES (7, 'Ontario', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 1);
INSERT INTO `first_level_divisions` VALUES (8, 'Prince Edward Island', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 1);
INSERT INTO `first_level_divisions` VALUES (9, 'Quebec', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 1);
INSERT INTO `first_level_divisions` VALUES (10, 'Saskatchewan', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 1);
INSERT INTO `first_level_divisions` VALUES (11, 'London', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (12, 'North East', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (13, 'North West', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (14, 'Yorkshire', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (15, 'East Midlands', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (16, 'West Midlands', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (17, 'South East', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (18, 'East of England', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (19, 'South West', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (20, 'Scotland', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (21, 'Wales', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (22, 'Guernsey', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (23, 'Jersey', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (24, 'Isle of Man ', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (25, 'Akrotiri', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (26, 'Anguilla', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (27, 'Bermuda', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (28, 'British Indian Ocean Territory', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (29, 'British Virgin Islands', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (30, 'Cayman Islands', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (31, 'Falkland Islands', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (32, 'Gibraltar', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (33, 'Montserrat', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (34, 'Pitcairn Islands', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (35, 'Dhekelia', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (36, ' Saint Helena', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (37, 'Ascension', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (38, 'Tristan da Cunha', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 2);
INSERT INTO `first_level_divisions` VALUES (39, 'Palmyra', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 3);
INSERT INTO `first_level_divisions` VALUES (40, 'Atoll', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 3);
INSERT INTO `first_level_divisions` VALUES (41, 'Guam', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 3);
INSERT INTO `first_level_divisions` VALUES (42, 'Northern Mariana Islands', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 3);
INSERT INTO `first_level_divisions` VALUES (43, 'Puerto Rico', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 3);
INSERT INTO `first_level_divisions` VALUES (44, 'United States Virgin Islands', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 3);
INSERT INTO `first_level_divisions` VALUES (45, 'New York', '2021-05-23 21:52:56', 'test', '2021-05-23 21:52:56', 'test', 3);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `User_ID` int NOT NULL AUTO_INCREMENT,
  `User_Name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Unique',
  `Password` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `Create_Date` datetime NULL DEFAULT NULL,
  `Created_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Last_Update` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `Last_Updated_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`User_ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'test', 'test', '2021-05-23 01:34:36', 'test', '2021-05-23 01:34:40', 'test');

SET FOREIGN_KEY_CHECKS = 1;
