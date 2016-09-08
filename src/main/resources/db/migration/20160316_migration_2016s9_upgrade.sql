use campaign_db;

alter table campaign_db.flashsale_product add is_available Boolean DEFAULT true after product_key;
alter table campaign_db.flashsale_variant add is_available Boolean DEFAULT true after variant_id;

