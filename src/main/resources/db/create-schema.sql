DROP DATABASE IF EXISTS campaign_db;
CREATE DATABASE campaign_db;

GRANT ALL ON campaign_db.* to 'campaign'@'localhost' identified by 'campaign@dm1n';
GRANT ALL ON campaign_db.* to 'campaign'@'127.0.0.1' identified by 'campaign@dm1n';
