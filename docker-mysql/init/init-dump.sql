-- MySQL dump 10.13  Distrib 9.5.0, for macos15.7 (arm64)
--
-- Host: localhost    Database: family_tree
-- ------------------------------------------------------
-- Server version	9.5.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '21564b6c-d289-11f0-996d-5cc8d9e5f003:1-35';

--
-- Current Database: `family_tree`
--

/*!40000 DROP DATABASE IF EXISTS `family_tree`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `family_tree` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `family_tree`;

--
-- Table structure for table `digital_legacy_assets`
--

DROP TABLE IF EXISTS `digital_legacy_assets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `digital_legacy_assets` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `person_id` bigint NOT NULL COMMENT '用户的唯一身份识别ID',
  `ipfs_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '内容在IPFS上的内容哈希(CID)',
  `conflux_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '在Conflux链上的交易哈希或合约存证指纹',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_person_id` (`person_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `digital_legacy_assets`
--

LOCK TABLES `digital_legacy_assets` WRITE;
/*!40000 ALTER TABLE `digital_legacy_assets` DISABLE KEYS */;
INSERT INTO `digital_legacy_assets` VALUES (1,55206179,'QmTaoeGQbGh993xzJM536pd1Vj1NHH7CqkEra5eCgGmC74',NULL,'2025-12-21 06:52:58');
/*!40000 ALTER TABLE `digital_legacy_assets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','<< Flyway Baseline >>','BASELINE','<< Flyway Baseline >>',NULL,'root','2025-12-16 16:26:07',0,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_login`
--

DROP TABLE IF EXISTS `t_login`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_login` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `person_id` bigint NOT NULL,
  `password` varchar(32) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `person_id` (`person_id`),
  CONSTRAINT `fk_person` FOREIGN KEY (`person_id`) REFERENCES `t_person` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_login`
--

LOCK TABLES `t_login` WRITE;
/*!40000 ALTER TABLE `t_login` DISABLE KEYS */;
INSERT INTO `t_login` VALUES (3,3,'1234'),(4,4,'1234'),(5,5,'1234'),(6,6,'6789'),(7,7,'6789');
/*!40000 ALTER TABLE `t_login` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_person`
--

DROP TABLE IF EXISTS `t_person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_person` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `level` int DEFAULT '0',
  `gender` char(1) DEFAULT '1' COMMENT '1:男, 0:女',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_person`
--

LOCK TABLES `t_person` WRITE;
/*!40000 ALTER TABLE `t_person` DISABLE KEYS */;
INSERT INTO `t_person` VALUES (3,'zhansan',0,'1'),(4,'wanger',1,'1'),(5,'lisi',2,'1'),(6,'张菊',0,'1'),(7,'李月',0,'0');
/*!40000 ALTER TABLE `t_person` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_relationship`
--

DROP TABLE IF EXISTS `t_relationship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_relationship` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `from_person_id` bigint NOT NULL,
  `to_person_id` bigint NOT NULL,
  `generation_gap` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_relation` (`from_person_id`,`to_person_id`),
  KEY `idx_from` (`from_person_id`),
  KEY `idx_to` (`to_person_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_relationship`
--

LOCK TABLES `t_relationship` WRITE;
/*!40000 ALTER TABLE `t_relationship` DISABLE KEYS */;
INSERT INTO `t_relationship` VALUES (1,3,4,1),(8,3,5,2),(9,3,7,1);
/*!40000 ALTER TABLE `t_relationship` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

CREATE TABLE `digital_asset_details` (
                                         `file_id` BIGINT NOT NULL COMMENT '文件ID，关联主表digital_legacy_assets的id',
                                         `description` TEXT COMMENT '文件的详细描述信息',
                                         `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '详情信息的创建/录入时间',

    -- 1. 设为主键：物理上保证了 file_id 绝对不可重复，且非空
                                         PRIMARY KEY (`file_id`),

    -- 2. 设置外键：确保这个 file_id 必须在主表里存在，才能往这里插数据
                                         CONSTRAINT `fk_asset_detail_file_id`
                                             FOREIGN KEY (`file_id`)
                                                 REFERENCES `digital_legacy_assets` (`id`)
                                                 ON DELETE CASCADE -- (可选) 如果主表文件删了，详情自动删除，防止产生垃圾数据
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数字资产详情表';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-03 14:28:04
