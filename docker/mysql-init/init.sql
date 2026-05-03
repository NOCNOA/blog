-- Blog Database Initialization Script
-- This runs automatically on MySQL container first start

CREATE DATABASE IF NOT EXISTS `blog`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `blog`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `article_tag`;
DROP TABLE IF EXISTS `article`;
DROP TABLE IF EXISTS `tag`;
DROP TABLE IF EXISTS `category`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `site_config`;
DROP TABLE IF EXISTS `operation_log`;

CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '登录用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '登录密码',
  `nickname` VARCHAR(50) NOT NULL COMMENT '昵称',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_username` (`username`),
  UNIQUE KEY `uk_user_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='后台用户表';

CREATE TABLE `category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '分类描述',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序值, 越小越靠前',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章分类表';

CREATE TABLE `tag` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tag_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签表';

CREATE TABLE `article` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` VARCHAR(150) NOT NULL COMMENT '文章标题',
  `summary` VARCHAR(500) DEFAULT NULL COMMENT '文章摘要',
  `content` LONGTEXT NOT NULL COMMENT '文章正文',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图URL',
  `category_id` BIGINT NOT NULL COMMENT '所属分类ID',
  `author_id` BIGINT NOT NULL COMMENT '作者ID',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-草稿, 1-已发布, 2-已下线',
  `is_top` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶: 0-否, 1-是',
  `view_count` BIGINT NOT NULL DEFAULT 0 COMMENT '浏览量',
  `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_article_category_id` (`category_id`),
  KEY `idx_article_author_id` (`author_id`),
  KEY `idx_article_status` (`status`),
  KEY `idx_article_publish_time` (`publish_time`),
  KEY `idx_article_is_top_publish_time` (`is_top`, `publish_time`),
  CONSTRAINT `fk_article_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`),
  CONSTRAINT `fk_article_author` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

CREATE TABLE `article_tag` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `article_id` BIGINT NOT NULL COMMENT '文章ID',
  `tag_id` BIGINT NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_tag_article_id_tag_id` (`article_id`, `tag_id`),
  KEY `idx_article_tag_tag_id` (`tag_id`),
  CONSTRAINT `fk_article_tag_article` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_article_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

CREATE TABLE IF NOT EXISTS `site_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_name` VARCHAR(100) NOT NULL COMMENT '站点名称',
  `site_logo` VARCHAR(255) DEFAULT NULL COMMENT '站点Logo',
  `site_description` VARCHAR(255) DEFAULT NULL COMMENT '站点描述',
  `site_notice` VARCHAR(500) DEFAULT NULL COMMENT '站点公告',
  `footer_info` VARCHAR(255) DEFAULT NULL COMMENT '页脚信息',
  `github_url` VARCHAR(255) DEFAULT NULL COMMENT 'GitHub地址',
  `gitee_url` VARCHAR(255) DEFAULT NULL COMMENT 'Gitee地址',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '博主头像',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站点配置表';

CREATE TABLE IF NOT EXISTS `operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `module` VARCHAR(50) NOT NULL COMMENT '模块名称',
  `operation` VARCHAR(100) NOT NULL COMMENT '操作名称',
  `request_method` VARCHAR(20) DEFAULT NULL COMMENT '请求方法',
  `request_uri` VARCHAR(255) DEFAULT NULL COMMENT '请求URI',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
  `operator_name` VARCHAR(50) DEFAULT NULL COMMENT '操作人名称',
  `ip` VARCHAR(64) DEFAULT NULL COMMENT '请求IP',
  `params` TEXT DEFAULT NULL COMMENT '请求参数',
  `success` TINYINT NOT NULL DEFAULT 1 COMMENT '是否成功: 0-失败, 1-成功',
  `error_message` VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_operation_log_module` (`module`),
  KEY `idx_operation_log_operator_id` (`operator_id`),
  KEY `idx_operation_log_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- Default admin user (password: admin123)
INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `status`)
VALUES ('admin', 'admin123', '管理员', 'admin@example.com', 1);

-- Default categories
INSERT INTO `category` (`name`, `description`, `sort`)
VALUES
  ('后端开发', 'Spring Boot、MyBatis、数据库等相关文章', 1),
  ('生活随笔', '日常记录与想法整理', 2);

-- Default tags
INSERT INTO `tag` (`name`)
VALUES ('Java'), ('Spring Boot'), ('MyBatis'), ('MySQL');

-- Default site config
INSERT INTO `site_config` (
  `site_name`, `site_description`, `site_notice`, `footer_info`
) VALUES (
  '一嘉的博客', '记录开发、学习与生活', '欢迎来到我的博客', '© 2026 一嘉的博客'
);

SET FOREIGN_KEY_CHECKS = 1;
