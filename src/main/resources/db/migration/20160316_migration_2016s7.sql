use campaign_db;
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS flash_sale;
DROP TABLE IF EXISTS  flashsale_variant;
DROP TABLE IF EXISTS  flashsale_product;
DROP TABLE IF EXISTS  flashsale_category;
SET FOREIGN_KEY_CHECKS=1;


CREATE TABLE `flash_sale` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `banner_images` longtext COLLATE utf8_unicode_ci,
  `condition_data` longtext COLLATE utf8_unicode_ci NOT NULL,
  `enable` tinyint(1) NOT NULL,
  `end_period` datetime NOT NULL,
  `member` tinyint(1) NOT NULL,
  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `name_translation` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `non_member` tinyint(1) NOT NULL,
  `partner` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `short_description` varchar(140) COLLATE utf8_unicode_ci DEFAULT NULL,
  `short_description_translation` varchar(140) COLLATE utf8_unicode_ci DEFAULT NULL,
  `start_period` datetime NOT NULL,
  `type` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `app_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_FLASH_SALE_APP_ID` (`app_id`),
  CONSTRAINT `FK_FLASH_SALE_APP_ID` FOREIGN KEY (`app_id`) REFERENCES `app_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


CREATE TABLE `flashsale_product` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `max_discount_percent` double NOT NULL,
  `max_promotion_price` double NOT NULL,
  `min_discount_percent` double NOT NULL,
  `min_promotion_price` double NOT NULL,
  `product_key` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `flash_sale_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_FLASH_SALE_PRODUCT_FLASH_SALE` (`flash_sale_id`),
  CONSTRAINT `FK_FLASH_SALE_PRODUCT_FLASH_SALE` FOREIGN KEY (`flash_sale_id`) REFERENCES `flash_sale` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


CREATE TABLE `flashsale_variant` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `discount_percent` double NOT NULL,
  `limit_quantity` bigint(20) DEFAULT NULL,
  `promotion_price` double NOT NULL,
  `variant_id` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `product_key` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_FLASH_SALE_VARIANT_FLASH_SALE_PRODUCT` (`product_key`),
  CONSTRAINT `FK_FLASH_SALE_VARIANT_FLASH_SALE_PRODUCT` FOREIGN KEY (`product_key`) REFERENCES `flashsale_product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



CREATE TABLE `flashsale_category` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `category_id` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `product_key` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_FLASH_SALE_CATEGORY_FLASH_SALE_PRODUCT` (`product_key`),
  CONSTRAINT `FK_FLASH_SALE_CATEGORY_FLASH_SALE_PRODUCT` FOREIGN KEY (`product_key`) REFERENCES `flashsale_product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
