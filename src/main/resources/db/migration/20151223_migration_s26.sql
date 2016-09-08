use campaign_db;

CREATE TABLE if not exists `campaign` ( `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
`created_at`datetime DEFAULT NULL,
`created_by`varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
`updated_at`datetime DEFAULT NULL,
`updated_by`varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
`detail`longtext COLLATE utf8_unicode_ci,
`detail_translation`longtext COLLATE utf8_unicode_ci,
`enable`tinyint(1) NOT NULL,
`end_period`datetime DEFAULT NULL,
`name`varchar(100) COLLATE utf8_unicode_ci NOT NULL,
`name_translation`varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
`start_period`datetime DEFAULT NULL,
PRIMARY KEY (`id`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

INSERT INTO `campaign_db`.`campaign`
(`id`,
`created_at`,
`updated_at`,
`detail`,
`detail_translation`,
`enable`,
`end_period`,
`name`,
`name_translation`,
`start_period`)
values
('1',
'2015-12-22 18:24:31',
'2015-12-22 18:24:31',
'Default Campaign for  bundle ',
'Default Campaign for  bundle ',
'1',
'2016-03-31 14:54:00',
'Default Campaign bundle',
'Default Campaign bundle',
'2015-12-01 14:54:00')
on duplicate key update id = 1;

INSERT INTO `campaign_db`.`campaign`
(`id`,
`created_at`,
`updated_at`,
`detail`,
`detail_translation`,
`enable`,
`end_period`,
`name`,
`name_translation`,
`start_period`)
values
('2',
'2015-12-22 18:24:31',
'2015-12-22 18:24:31',
'Default Campaign for freebie ',
'Default Campaign for freebie ',
'1',
'2016-03-31 14:54:00',
'Default Campaign freebie',
'Default Campaign freebie',
'2015-12-01 14:54:00')
on duplicate key update id = 2;

alter table promotion add campaign_id bigint(20) unsigned not null default 0 after id;

SET FOREIGN_KEY_CHECKS = 0;
alter table promotion add constraint fk_promotion_campaign foreign key (campaign_id) references campaign(id);

set sql_safe_updates=0;
update promotion set campaign_id='1' where promotion_type ='itm-bundle';
update promotion set campaign_id='2' where promotion_type ='itm-freebie';

-- Audit log
drop table if exists campaign_aud;
drop table if exists promotion_aud;
drop table if exists promotion_wm_aud;
drop table if exists revinfo;


