use campaign_db;

CREATE TABLE if not exists`version_migrations` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `migrations_note` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `version_migrations` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

create table if not exists code_vip (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  code varchar(15),
  email varchar(100),
  created_at timestamp,
  created_by varchar(100),
  updated_at timestamp,
  updated_by varchar(100),
  primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

alter table promotion add detail_data longtext after condition_data;
alter table pending_promotion add detail_data longtext after condition_data;
alter table code_detail add time_per_user INT(10);
alter table code_detail add format VARCHAR(10) DEFAULT 'fixed';
alter table code_detail add format_suffix INT(10) DEFAULT 0;
alter table code_detail add format_prefix VARCHAR(10) DEFAULT '-';
alter table code_detail add type_of_limitation VARCHAR(10) DEFAULT 'unlimited';
alter table code add time_per_user INT(10);
