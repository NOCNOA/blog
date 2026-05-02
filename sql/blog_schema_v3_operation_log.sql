USE `blog`;

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
