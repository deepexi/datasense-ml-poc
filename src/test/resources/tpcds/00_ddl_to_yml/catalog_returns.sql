CREATE TABLE public.catalog_returns (
cr_returned_date_sk int8 NULL,
cr_returned_time_sk int8 NULL,
cr_item_sk int8 NULL,
cr_refunded_customer_sk int8 NULL,
cr_refunded_cdemo_sk int8 NULL,
cr_refunded_hdemo_sk int8 NULL,
cr_refunded_addr_sk int8 NULL,
cr_returning_customer_sk int8 NULL,
cr_returning_cdemo_sk int8 NULL,
cr_returning_hdemo_sk int8 NULL,
cr_returning_addr_sk int8 NULL,
cr_call_center_sk int8 NULL,
cr_catalog_page_sk int8 NULL,
cr_ship_mode_sk int8 NULL,
cr_warehouse_sk int8 NULL,
cr_reason_sk int8 NULL,
cr_order_number int8 NULL,
cr_return_quantity int4 NULL,
cr_return_amount numeric(7, 2) NULL,
cr_return_tax numeric(7, 2) NULL,
cr_return_amt_inc_tax numeric(7, 2) NULL,
cr_fee numeric(7, 2) NULL,
cr_return_ship_cost numeric(7, 2) NULL,
cr_refunded_cash numeric(7, 2) NULL,
cr_reversed_charge numeric(7, 2) NULL,
cr_store_credit numeric(7, 2) NULL,
cr_net_loss numeric(7, 2) NULL
);
