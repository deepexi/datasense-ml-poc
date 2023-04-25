CREATE TABLE public.item (
i_item_sk int8 NULL,
i_item_id varchar(16) NULL,
i_rec_start_date date NULL,
i_rec_end_date date NULL,
i_item_desc varchar(200) NULL,
i_current_price numeric(7, 2) NULL,
i_wholesale_cost numeric(7, 2) NULL,
i_brand_id int4 NULL,
i_brand varchar(50) NULL,
i_class_id int4 NULL,
i_class varchar(50) NULL,
i_category_id int4 NULL,
i_category varchar(50) NULL,
i_manufact_id int4 NULL,
i_manufact varchar(50) NULL,
i_size varchar(20) NULL,
i_formulation varchar(20) NULL,
i_color varchar(20) NULL,
i_units varchar(10) NULL,
i_container varchar(10) NULL,
i_manager_id int4 NULL,
i_product_name varchar(50) NULL
);
