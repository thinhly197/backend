use campaign_db;

DROP TABLE IF EXISTS `flash_sale`;


CREATE TABLE `app_id` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `description` varchar(140) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `APP_ID_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


INSERT INTO `campaign_db`.`app_id`
(`id`,
`description`,
`name`)
VALUES
('1',
'iTruemart App id',
'iTruemart')
on duplicate key update id = 1;

INSERT INTO `campaign_db`.`app_id`
(`id`,
`description`,
`name`)
VALUES
('6',
'Exclusive Privilege App id',
'Exclusive Privilege')
on duplicate key update id = 6;

INSERT INTO `campaign_db`.`app_id`
(`id`,
`description`,
`name`)
VALUES
('9',
'TruemoveH App id',
'TruemoveH')
on duplicate key update id = 9;



CREATE TABLE `flash_sale` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(100) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(100) DEFAULT NULL,
  `banner_images` longtext,
  `condition_data` longtext NOT NULL,
  `enable` tinyint(1) NOT NULL,
  `end_period` datetime NOT NULL,
  `member` tinyint(1) NOT NULL,
  `name` varchar(100) NOT NULL,
  `name_translation` varchar(100) DEFAULT NULL,
  `non_member` tinyint(1) NOT NULL,
  `partner` varchar(50) DEFAULT NULL,
  `short_description` varchar(140) DEFAULT NULL,
  `short_description_translation` varchar(140) DEFAULT NULL,
  `start_period` datetime NOT NULL,
  `type` varchar(50) NOT NULL,
  `app_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_FLASH_SALE_APP_ID` (`app_id`),
  CONSTRAINT `FK_FLASH_SALE_APP_ID` FOREIGN KEY (`app_id`) REFERENCES `app_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ;



CREATE TABLE `flashsale_variant` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `category_code` varchar(255) DEFAULT NULL,
  `limit_quantity` bigint(20) DEFAULT NULL,
  `promotion_price` double DEFAULT NULL,
  `variant_id` varchar(255) DEFAULT NULL,
  `flash_sale_id` bigint(20) unsigned NOT NULL,
  `category_id` varchar(255) DEFAULT NULL,
  `discount_percent` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_FLASH_SALE_VARIANT_APP_ID` (`flash_sale_id`),
  CONSTRAINT `FK_FLASH_SALE_VARIANT_APP_ID` FOREIGN KEY (`flash_sale_id`) REFERENCES `flash_sale` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ;



