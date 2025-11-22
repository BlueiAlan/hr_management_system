-- MySQL dump 10.13  Distrib 5.7.24, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: hr_management_system
-- ------------------------------------------------------
-- Server version	5.7.24-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `employee` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `archive_number` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '档案编号',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
  `password` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` tinyint(1) DEFAULT '0' COMMENT '1-管理员，0-普通成员',
  `delete_msg` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除原因',
  `gender` enum('男','女') COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '性别',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电子邮箱',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电话',
  `qq` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mobile` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机',
  `address` text COLLATE utf8mb4_unicode_ci COMMENT '住址',
  `postal_code` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮编',
  `nationality` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '国籍',
  `birth_place` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '出生地',
  `birth_date` date DEFAULT NULL COMMENT '出生时间',
  `ethnicity` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '民族',
  `religion` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '宗教信仰',
  `political_status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '政治面貌',
  `id_card` varchar(18) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '身份证',
  `age` int(11) DEFAULT NULL,
  `education` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学历',
  `salary_standard_id` int(11) DEFAULT NULL COMMENT '薪酬标准ID',
  `hobbies` text COLLATE utf8mb4_unicode_ci COMMENT '爱好',
  `resume` text COLLATE utf8mb4_unicode_ci COMMENT '个人履历',
  `family_info` text COLLATE utf8mb4_unicode_ci COMMENT '家庭关系信息',
  `remarks` text COLLATE utf8mb4_unicode_ci COMMENT '备注',
  `photo_path` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '照片路径',
  `org_id` int(11) NOT NULL COMMENT '所属三级机构ID',
  `position_id` int(11) NOT NULL COMMENT '职位ID',
  `title` enum('初级','中级','高级') COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职称',
  `status` enum('待复核','正常','已删除','不通过') COLLATE utf8mb4_unicode_ci DEFAULT '待复核',
  `created_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '登记人',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `reviewed_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '复核人',
  `reviewed_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` timestamp NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `archive_number` (`archive_number`),
  UNIQUE KEY `id_card` (`id_card`),
  KEY `salary_standard_id` (`salary_standard_id`),
  KEY `idx_employees_org_id` (`org_id`),
  KEY `idx_employees_position_id` (`position_id`),
  KEY `idx_employees_status` (`status`),
  CONSTRAINT `employee_ibfk_1` FOREIGN KEY (`org_id`) REFERENCES `organizations` (`id`),
  CONSTRAINT `employee_ibfk_2` FOREIGN KEY (`position_id`) REFERENCES `positions` (`id`),
  CONSTRAINT `employee_ibfk_3` FOREIGN KEY (`salary_standard_id`) REFERENCES `salary_standards` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='档案编号';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES (1,'20250101010101','张三','e10adc3949ba59abbe56e057f20f883e',1,NULL,'男','zhangsan@company.com',NULL,NULL,'13800138000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,3,1,'中级','正常','admin','2025-10-31 01:45:22',NULL,NULL,'2025-11-21 04:10:58',NULL),(2,'20250101010102','李四','e10adc3949ba59abbe56e057f20f883e',2,NULL,'女','lisi@company.com',NULL,NULL,'13900139000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,NULL,NULL,NULL,NULL,NULL,3,2,'初级','正常','admin','2025-10-31 01:45:22','admin','2025-11-05 09:38:19','2025-11-21 04:10:58',NULL),(3,'20250000000000','admin','e10adc3949ba59abbe56e057f20f883e',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,1,'高级','正常','admin','2025-11-03 08:37:48',NULL,NULL,'2025-11-21 04:10:58',NULL),(4,'2025010101103','陈芳','e10adc3949ba59abbe56e057f20f883e',3,NULL,'女','','','','','','','','',NULL,'汉族','','','',10,'',NULL,'','','','','',3,7,'高级','正常','admin','2025-11-07 02:41:43','admin','2025-11-07 03:31:22','2025-11-21 04:10:58',NULL),(9,'202501010201','李思','e10adc3949ba59abbe56e057f20f883e',4,NULL,'女','','','','','','','中国','','1995-06-06','汉族','','群众',NULL,30,'',1,'','','','','',4,9,'中级','正常','admin','2025-11-12 01:10:00','admin','2025-11-12 01:28:04','2025-11-21 04:10:58',NULL),(10,'2025010101104','陈思思','e10adc3949ba59abbe56e057f20f883e',1,NULL,'女','','','','','','','中国','',NULL,'','','',NULL,0,'硕士',NULL,'','','','','',3,7,'中级','正常','admin','2025-11-12 02:39:59','admin','2025-11-12 02:40:11','2025-11-21 04:10:58',NULL),(11,'202501010202','黄橙橙','e10adc3949ba59abbe56e057f20f883e',2,'员工离职','女','','','','','','','中国','',NULL,'','','',NULL,0,'',1,'','','','','',4,9,'中级','正常','admin','2025-11-12 02:47:50','admin','2025-11-12 02:53:21','2025-11-21 04:10:58','2025-11-12 03:30:57'),(12,'202501020301','陈房','e10adc3949ba59abbe56e057f20f883e',3,'员工离职','男','','','','','','','中国','',NULL,'','','',NULL,0,'',1,'','','','','',10,8,'高级','已删除','admin','2025-11-12 03:03:31','admin','2025-11-12 03:03:37','2025-11-21 04:10:58','2025-11-21 03:04:58');
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organizations`
--

DROP TABLE IF EXISTS `organizations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organizations` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `org_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '机构编码',
  `org_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '机构名称',
  `org_level` int(11) NOT NULL COMMENT '机构级别：1-一级机构，2-二级机构，3-三级机构',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父级机构id',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` int(11) DEFAULT '0' COMMENT '0-未删除，1-删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `org_code` (`org_code`),
  KEY `idx_organizations_parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organizations`
--

LOCK TABLES `organizations` WRITE;
/*!40000 ALTER TABLE `organizations` DISABLE KEYS */;
INSERT INTO `organizations` VALUES (1,'01','总部',1,NULL,'2025-10-31 01:45:22','2025-10-31 01:45:22',0),(2,'0101','技术部',2,1,'2025-10-31 01:45:22','2025-10-31 01:45:22',0),(3,'010101','开发一组',3,2,'2025-10-31 01:45:22','2025-10-31 01:45:22',0),(4,'010102','开发二组',3,2,'2025-10-31 01:45:22','2025-10-31 01:45:22',0),(5,'0102','销售部',2,1,'2025-10-31 01:45:22','2025-10-31 15:43:27',0),(6,'010201','销售一部',3,5,'2025-10-31 01:45:22','2025-10-31 15:42:30',0),(8,'010202','销售二部',3,5,'2025-10-31 14:39:37','2025-10-31 15:42:30',0),(10,'010203','销售三部',3,5,'2025-10-31 14:41:28','2025-10-31 15:42:30',0),(11,'010103','开发三组',3,2,'2025-10-31 14:44:05','2025-10-31 14:44:05',0);
/*!40000 ALTER TABLE `organizations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `positions`
--

DROP TABLE IF EXISTS `positions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `positions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `position_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '职位名称',
  `org_id` bigint(20) DEFAULT NULL COMMENT '所属三级机构id',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '职位描述',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` int(11) DEFAULT '0' COMMENT '0-未删除，1-删除',
  PRIMARY KEY (`id`),
  KEY `org_id` (`org_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `positions`
--

LOCK TABLES `positions` WRITE;
/*!40000 ALTER TABLE `positions` DISABLE KEYS */;
INSERT INTO `positions` VALUES (1,'Java开发工程师',3,'负责Java后端开发','2025-10-31 01:45:22','2025-10-31 01:45:22',0),(2,'前端开发工程师',3,'负责前端页面开发','2025-10-31 01:45:22','2025-10-31 01:45:22',0),(3,'项目经理',4,'负责项目管理工作','2025-10-31 01:45:22','2025-10-31 01:45:22',0),(4,'销售专员',6,'负责产品销售','2025-10-31 01:45:22','2025-10-31 01:45:22',0),(5,'Java开发工程师',3,'负责Java后端开发，同时运营','2025-10-31 06:53:29','2025-11-02 09:56:58',0),(6,'前端开发工程师',3,'负责前端页面开发','2025-10-31 06:53:29','2025-10-31 06:53:29',0),(7,'项目经理',4,'负责项目管理工作','2025-10-31 06:53:29','2025-10-31 06:53:29',0),(8,'销售专员',6,'负责产品销售','2025-10-31 06:53:29','2025-10-31 06:53:29',0),(9,'前端开发工程师',4,'负责项目的前端的渲染','2025-10-31 15:06:24','2025-11-02 09:57:16',0);
/*!40000 ALTER TABLE `positions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `salary_issue_details`
--

DROP TABLE IF EXISTS `salary_issue_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `salary_issue_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `issue_id` bigint(20) DEFAULT NULL COMMENT '薪酬发放id',
  `employee_id` bigint(20) DEFAULT NULL COMMENT '员工id',
  `salary_standard_id` bigint(20) DEFAULT NULL COMMENT '薪酬标准id',
  `reward_amount` decimal(10,2) DEFAULT '0.00' COMMENT '奖励金额',
  `deduction_amount` decimal(10,2) DEFAULT '0.00' COMMENT '应扣金额',
  `final_amount` decimal(10,2) DEFAULT NULL COMMENT '实发金额',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `issue_id` (`issue_id`),
  KEY `employee_id` (`employee_id`),
  KEY `salary_standard_id` (`salary_standard_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1966518274 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `salary_issue_details`
--

LOCK TABLES `salary_issue_details` WRITE;
/*!40000 ALTER TABLE `salary_issue_details` DISABLE KEYS */;
INSERT INTO `salary_issue_details` VALUES (-1615372286,-1615372287,4,1,0.00,0.00,15000.00,'2025-11-12 01:51:06','2025-11-12 01:51:06'),(-1615372285,-1615372287,1,1,0.00,0.00,15000.00,'2025-11-12 01:51:06','2025-11-12 01:51:06'),(-646504446,-667475967,9,1,0.00,0.00,15000.00,'2025-11-12 02:38:38','2025-11-12 02:38:38'),(-138993663,1,1,1,1000.00,500.00,15500.00,'2025-11-05 09:36:18','2025-11-05 09:36:18'),(-138993662,1,2,2,501.00,300.00,201.00,'2025-11-05 09:36:18','2025-11-05 09:36:18'),(-138993661,1,1,1,1000.00,500.00,15500.00,'2025-11-05 09:36:18','2025-11-05 09:36:18'),(-138993660,1,2,2,500.00,300.00,10200.00,'2025-11-05 09:36:18','2025-11-05 09:36:18'),(171401217,133652482,9,1,0.00,200.00,14800.00,'2025-11-12 01:40:44','2025-11-12 01:40:44'),(234315778,133652482,4,1,0.00,200.00,14800.00,'2025-11-12 01:40:44','2025-11-12 01:40:44'),(418865154,351756290,4,1,0.00,0.00,15000.00,'2025-11-12 02:05:15','2025-11-12 02:05:15'),(1966518273,1953935361,9,1,0.00,0.00,15000.00,'2025-11-12 02:17:22','2025-11-12 02:17:22');
/*!40000 ALTER TABLE `salary_issue_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `salary_issues`
--

DROP TABLE IF EXISTS `salary_issues`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `salary_issues` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `issue_number` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '薪酬单号',
  `org_id` bigint(20) DEFAULT NULL COMMENT '发放机构',
  `issue_date` date NOT NULL COMMENT '发放日期',
  `total_employees` int(11) DEFAULT NULL COMMENT '总人数',
  `total_amount` decimal(12,2) DEFAULT NULL COMMENT '总额',
  `review_opinion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '复核意见',
  `status` enum('待复核','已复核','已发放','不通过') COLLATE utf8mb4_unicode_ci DEFAULT '待复核',
  `created_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '登记人',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `reviewed_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '复核人',
  `reviewed_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `issue_number` (`issue_number`),
  KEY `idx_salary_issues_org_id` (`org_id`),
  KEY `idx_salary_issues_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1953935362 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `salary_issues`
--

LOCK TABLES `salary_issues` WRITE;
/*!40000 ALTER TABLE `salary_issues` DISABLE KEYS */;
INSERT INTO `salary_issues` VALUES (-1615372287,'ISS202511002',3,'2025-11-12',2,30000.00,NULL,'已复核','admin','2025-11-12 01:51:06','admin','2025-11-12 01:51:12'),(-667475967,'ISS202511005',4,'2025-11-12',1,15000.00,'不行','已复核','admin','2025-11-12 02:38:38','admin','2025-11-12 02:38:51'),(1,'ISSUE202501001',3,'2025-01-15',4,41401.00,NULL,'已发放','薪酬专员','2025-10-31 01:45:22','admin','2025-11-07 01:32:02'),(133652482,'ISS202511001',3,'2025-11-12',2,29600.00,NULL,'已发放','admin','2025-11-12 01:40:44','admin','2025-11-12 01:41:19'),(351756290,'ISS202511003',3,'2025-11-12',1,15000.00,NULL,'已复核','admin','2025-11-12 02:05:15','admin','2025-11-12 02:13:04'),(1953935361,'ISS202511004',4,'2025-11-12',1,15000.00,'','不通过','admin','2025-11-12 02:17:22','admin','2025-11-12 02:33:24');
/*!40000 ALTER TABLE `salary_issues` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `salary_items`
--

DROP TABLE IF EXISTS `salary_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `salary_items` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `item_num` char(4) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '项目编号',
  `item_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '薪酬项目名称',
  `item_type` enum('收入','扣除') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '项目类型',
  `calculation_rule` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '计算规则',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '0-未删除，1-删除',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `salary_items`
--

LOCK TABLES `salary_items` WRITE;
/*!40000 ALTER TABLE `salary_items` DISABLE KEYS */;
INSERT INTO `salary_items` VALUES (1,'S001','基本工资','收入',NULL,1,0,'2025-10-31 01:45:22','2025-11-03 05:30:00'),(2,'S002','交通补助','收入',NULL,1,0,'2025-10-31 01:45:22','2025-11-03 05:30:18'),(3,'S003','午餐补助','收入',NULL,1,0,'2025-10-31 01:45:22','2025-10-31 16:24:35'),(4,'S004','通信补助','收入',NULL,1,0,'2025-10-31 01:45:22','2025-10-31 16:24:35'),(5,'S005','养老保险','扣除','基本工资 * 8%',1,0,'2025-10-31 01:45:22','2025-10-31 16:24:35'),(6,'S006','医疗保险','扣除','基本工资 * 2% + 3',1,0,'2025-10-31 01:45:22','2025-10-31 16:24:35'),(7,'S007','失业保险','扣除','基本工资 * 0.5%',1,0,'2025-10-31 01:45:22','2025-10-31 16:24:35'),(8,'S008','住房公积金','扣除','基本工资 * 8%',1,0,'2025-10-31 01:45:22','2025-10-31 16:24:35'),(17,'S009','晚餐补助','收入','',1,0,'2025-10-31 16:32:39','2025-10-31 16:32:39');
/*!40000 ALTER TABLE `salary_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `salary_standard_details`
--

DROP TABLE IF EXISTS `salary_standard_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `salary_standard_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `standard_id` bigint(20) DEFAULT NULL COMMENT '薪酬标准id',
  `salary_item_id` bigint(20) DEFAULT NULL COMMENT '薪酬项目id',
  `amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '金额',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `standard_id` (`standard_id`),
  KEY `salary_item_id` (`salary_item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=649551874 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `salary_standard_details`
--

LOCK TABLES `salary_standard_details` WRITE;
/*!40000 ALTER TABLE `salary_standard_details` DISABLE KEYS */;
INSERT INTO `salary_standard_details` VALUES (1,1,1,12000.00,'2025-10-31 01:45:22'),(2,1,2,500.00,'2025-10-31 01:45:22'),(3,1,3,300.00,'2025-10-31 01:45:22'),(4,1,4,200.00,'2025-10-31 01:45:22'),(5,2,1,8000.00,'2025-10-31 01:45:22'),(6,2,2,300.00,'2025-10-31 01:45:22'),(7,2,3,200.00,'2025-10-31 01:45:22'),(8,2,4,100.00,'2025-10-31 01:45:22'),(9,1,1,12000.00,'2025-10-31 06:53:29'),(10,1,2,500.00,'2025-10-31 06:53:29'),(11,1,3,300.00,'2025-10-31 06:53:29'),(12,1,4,200.00,'2025-10-31 06:53:29'),(13,2,1,8000.00,'2025-10-31 06:53:29'),(14,2,2,300.00,'2025-10-31 06:53:29'),(15,2,3,200.00,'2025-10-31 06:53:29'),(16,2,4,100.00,'2025-10-31 06:53:29'),(12046337,-931672062,1,15000.00,'2025-11-12 00:32:59'),(12046338,-931672062,2,200.00,'2025-11-12 00:32:59'),(12046339,-931672062,3,20.00,'2025-11-12 00:32:59'),(12046340,-931672062,17,20.00,'2025-11-12 00:32:59'),(12046341,-931672062,8,2000.00,'2025-11-12 00:32:59'),(582443010,582443009,1,3000.00,'2025-11-12 01:50:42'),(649551873,582443009,8,200.00,'2025-11-12 01:50:42');
/*!40000 ALTER TABLE `salary_standard_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `salary_standards`
--

DROP TABLE IF EXISTS `salary_standards`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `salary_standards` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `standard_number` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '薪酬标准编号',
  `standard_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '薪酬标准名称',
  `creator` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '制定人',
  `registrar` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登记人',
  `register_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登记时间',
  `total_amount` decimal(10,2) DEFAULT NULL COMMENT '薪酬总额',
  `status` enum('待复核','已生效','已停用') COLLATE utf8mb4_unicode_ci DEFAULT '待复核' COMMENT '状态：''待复核'', ''已生效'', ''已停用''',
  `reviewed_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '复核人',
  `reviewed_at` timestamp NULL DEFAULT NULL,
  `review_opinion` text COLLATE utf8mb4_unicode_ci COMMENT '复核意见',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `standard_number` (`standard_number`)
) ENGINE=InnoDB AUTO_INCREMENT=582443010 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `salary_standards`
--

LOCK TABLES `salary_standards` WRITE;
/*!40000 ALTER TABLE `salary_standards` DISABLE KEYS */;
INSERT INTO `salary_standards` VALUES (-931672062,'SAL202511001','高级开发工程师薪酬标准','张管理员','admin','2025-11-11 16:00:00',17240.00,'待复核',NULL,NULL,NULL,'2025-11-12 00:31:55','2025-11-12 00:32:59'),(1,'SAL2025001','中级开发工程师标准','王经理','李专员','2025-10-31 01:45:22',15000.00,'已生效',NULL,NULL,NULL,'2025-10-31 01:45:22','2025-10-31 01:45:22'),(2,'SAL2025002','初级开发工程师标准','王经理','李专员','2025-10-31 01:45:22',10000.00,'待复核',NULL,NULL,NULL,'2025-10-31 01:45:22','2025-10-31 01:45:22'),(582443009,'SAL202511002','销售部工资','张三','admin','2025-11-11 16:00:00',3200.00,'待复核',NULL,NULL,NULL,'2025-11-12 01:50:42','2025-11-12 01:50:42');
/*!40000 ALTER TABLE `salary_standards` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-22 16:07:11
