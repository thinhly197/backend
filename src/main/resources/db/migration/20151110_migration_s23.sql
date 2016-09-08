use campaign_db;
ALTER TABLE campaign_db.promotion
DROP COLUMN business_channel;

CREATE TABLE promotion_wm (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `app_ids` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `channel_type` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `condition_data` longtext COLLATE utf8_unicode_ci,
  `description_local` longtext COLLATE utf8_unicode_ci,
  `description_en` longtext COLLATE utf8_unicode_ci,
  `enable` tinyint(1) NOT NULL,
  `end_period` datetime DEFAULT NULL,
  `img_thm_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `img_thm_url_en` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `img_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `img_url_en` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `member` tinyint(1) NOT NULL,
  `name_local` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `name_en` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `non_member` tinyint(1) NOT NULL,
  `promotion_repeat` int(11) DEFAULT NULL,
  `short_description_local` varchar(140) COLLATE utf8_unicode_ci DEFAULT NULL,
  `short_description_en` varchar(140) COLLATE utf8_unicode_ci DEFAULT NULL,
  `start_period` datetime DEFAULT NULL,
  `promotion_type` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


CREATE TABLE `promotion_wm_aud` (
  `id` bigint(20) unsigned NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `app_ids` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `channel_type` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `condition_data` longtext COLLATE utf8_unicode_ci,
  `description_local` longtext COLLATE utf8_unicode_ci,
  `description_en` longtext COLLATE utf8_unicode_ci,
  `enable` tinyint(1) DEFAULT NULL,
  `end_period` datetime DEFAULT NULL,
  `img_thm_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `img_thm_url_en` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `img_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `img_url_en` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `member` tinyint(1) DEFAULT NULL,
  `name_local` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `name_en` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `non_member` tinyint(1) DEFAULT NULL,
  `promotion_repeat` int(11) DEFAULT NULL,
  `short_description_local` varchar(140) COLLATE utf8_unicode_ci DEFAULT NULL,
  `short_description_en` varchar(140) COLLATE utf8_unicode_ci DEFAULT NULL,
  `start_period` datetime DEFAULT NULL,
  `promotion_type` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK_rx7fl8dryywm9fpm743bb01l5` (`rev`),
  CONSTRAINT `FK_rx7fl8dryywm9fpm743bb01l5` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `deal`;

CREATE TABLE `deal` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `enable` tinyint(1) DEFAULT NULL,
  `channel_type` int(11) DEFAULT NULL,
  `condition_data` longtext COLLATE utf8_unicode_ci,
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `end_period` datetime DEFAULT NULL,
  `img_thm_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `img_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `short_description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `start_period` datetime DEFAULT NULL,
  `is_super_deal` tinyint(1) DEFAULT NULL,
  `description_local` longtext COLLATE utf8_unicode_ci,
  `description_en` longtext COLLATE utf8_unicode_ci,
  `img_thm_url_en` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `img_url_en` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `member` tinyint(1) NOT NULL,
  `name_local` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `name_en` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `non_member` tinyint(1) NOT NULL,
  `short_description_local` varchar(140) COLLATE utf8_unicode_ci DEFAULT NULL,
  `short_description_en` varchar(140) COLLATE utf8_unicode_ci DEFAULT NULL,
  `type` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


