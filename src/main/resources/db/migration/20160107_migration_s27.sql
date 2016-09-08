use campaign_db;

CREATE TABLE `pending_promotion` (
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
  `promotion_id` bigint(20) DEFAULT NULL,
  `promotion_repeat` int(11) DEFAULT NULL,
  `short_description_local` varchar(140) COLLATE utf8_unicode_ci DEFAULT NULL,
  `short_description_en` varchar(140) COLLATE utf8_unicode_ci DEFAULT NULL,
  `start_period` datetime DEFAULT NULL,
  `status` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `promotion_type` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `campaign_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ql5wvy7t3uuaihpt9qve92xll` (`campaign_id`),
  CONSTRAINT `FK_ql5wvy7t3uuaihpt9qve92xll` FOREIGN KEY (`campaign_id`) REFERENCES `campaign` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
