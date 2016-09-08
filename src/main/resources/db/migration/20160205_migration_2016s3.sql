use campaign_db;

CREATE TABLE if not exists  `email` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `emailgroup_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_emailgroup_email` (`emailgroup_id`,`email`),
  KEY `idx_emailgroup_id` (`emailgroup_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10001 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE if not exists `email_group` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `description` varchar(2000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE if not exists `flash_sale` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `condition_data` longtext COLLATE utf8_unicode_ci NOT NULL,
  `enable` tinyint(1) DEFAULT NULL,
  `end_period` datetime DEFAULT NULL,
  `incoming_banner` longtext COLLATE utf8_unicode_ci NOT NULL,
  `member` tinyint(1) NOT NULL,
  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `name_translation` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `non_member` tinyint(1) NOT NULL,
  `short_description` varchar(140) COLLATE utf8_unicode_ci DEFAULT NULL,
  `short_description_translation` varchar(140) COLLATE utf8_unicode_ci DEFAULT NULL,
  `start_period` datetime DEFAULT NULL,
  `today_banner` longtext COLLATE utf8_unicode_ci NOT NULL,
  `tomorrow_banner` longtext COLLATE utf8_unicode_ci NOT NULL,
  `type` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

drop table if exists code_vip;
