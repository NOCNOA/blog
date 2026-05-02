USE `blog`;

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

INSERT INTO `site_config` (
  `id`,
  `site_name`,
  `site_logo`,
  `site_description`,
  `site_notice`,
  `footer_info`,
  `github_url`,
  `gitee_url`,
  `avatar`
) VALUES (
  1,
  '一嘉的博客',
  NULL,
  '记录开发、学习与生活',
  '欢迎来到我的博客',
  '© 2026 一嘉的博客',
  NULL,
  NULL,
  NULL
)
ON DUPLICATE KEY UPDATE
  `site_name` = VALUES(`site_name`),
  `site_logo` = VALUES(`site_logo`),
  `site_description` = VALUES(`site_description`),
  `site_notice` = VALUES(`site_notice`),
  `footer_info` = VALUES(`footer_info`),
  `github_url` = VALUES(`github_url`),
  `gitee_url` = VALUES(`gitee_url`),
  `avatar` = VALUES(`avatar`);
