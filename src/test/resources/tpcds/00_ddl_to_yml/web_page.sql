CREATE TABLE public.web_page (
wp_web_page_sk int8 NULL,
wp_web_page_id varchar(16) NULL,
wp_rec_start_date date NULL,
wp_rec_end_date date NULL,
wp_creation_date_sk int8 NULL,
wp_access_date_sk int8 NULL,
wp_autogen_flag varchar(1) NULL,
wp_customer_sk int8 NULL,
wp_url varchar(100) NULL,
wp_type varchar(50) NULL,
wp_char_count int4 NULL,
wp_link_count int4 NULL,
wp_image_count int4 NULL,
wp_max_ad_count int4 NULL
);
