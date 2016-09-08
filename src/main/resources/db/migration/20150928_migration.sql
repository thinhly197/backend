use campaign_db;
alter table promotion add business_channel varchar(50) after channel_type;
alter table promotion add img_url_en varchar(255) after img_url;
alter table promotion add img_thm_url_en varchar(255) after img_url_en;
alter table promotion change column active enable tinyint(1) not null;
alter table promotion_aud change column active enable tinyint(1);
alter table deal change column active enable tinyint(1);
alter table deal modify is_super_deal tinyint(1);
alter table promotion_aud add business_channel varchar(50) after channel_type;
alter table promotion_aud add img_url_en varchar(255) after img_url;
alter table promotion_aud add img_thm_url_en varchar(255) after img_url_en;
update promotion set business_channel='itruemart';
update promotion set promotion_type='itm-bundle';
DROP TABLE IF EXISTS `promotion_task`;

CREATE TABLE `promotion_task` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `is_start` tinyint(1) DEFAULT NULL,
  `promotion_id` bigint(20) DEFAULT NULL,
  `trigger_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
