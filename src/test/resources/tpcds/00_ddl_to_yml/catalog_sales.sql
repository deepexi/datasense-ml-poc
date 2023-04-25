CREATE TABLE public.catalog_sales (
cs_sold_date_sk int8 NULL,
cs_sold_time_sk int8 NULL,
cs_ship_date_sk int8 NULL,
cs_bill_customer_sk int8 NULL,
cs_bill_cdemo_sk int8 NULL,
cs_bill_hdemo_sk int8 NULL,
cs_bill_addr_sk int8 NULL,
cs_ship_customer_sk int8 NULL,
cs_ship_cdemo_sk int8 NULL,
cs_ship_hdemo_sk int8 NULL,
cs_ship_addr_sk int8 NULL,
cs_call_center_sk int8 NULL,
cs_catalog_page_sk int8 NULL,
cs_ship_mode_sk int8 NULL,
cs_warehouse_sk int8 NULL,
cs_item_sk int8 NULL,
cs_promo_sk int8 NULL,
cs_order_number int8 NULL,
cs_quantity int4 NULL,
cs_wholesale_cost numeric(7, 2) NULL,
cs_list_price numeric(7, 2) NULL,
cs_sales_price numeric(7, 2) NULL,
cs_ext_discount_amt numeric(7, 2) NULL,
cs_ext_sales_price numeric(7, 2) NULL,
cs_ext_wholesale_cost numeric(7, 2) NULL,
cs_ext_list_price numeric(7, 2) NULL,
cs_ext_tax numeric(7, 2) NULL,
cs_coupon_amt numeric(7, 2) NULL,
cs_ext_ship_cost numeric(7, 2) NULL,
cs_net_paid numeric(7, 2) NULL,
cs_net_paid_inc_tax numeric(7, 2) NULL,
cs_net_paid_inc_ship numeric(7, 2) NULL,
cs_net_paid_inc_ship_tax numeric(7, 2) NULL,
cs_net_profit numeric(7, 2) NULL
);