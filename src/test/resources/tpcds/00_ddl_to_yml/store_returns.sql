CREATE TABLE public.store_returns (
sr_returned_date_sk int8 NULL,
sr_return_time_sk int8 NULL,
sr_item_sk int8 NULL,
sr_customer_sk int8 NULL,
sr_cdemo_sk int8 NULL,
sr_hdemo_sk int8 NULL,
sr_addr_sk int8 NULL,
sr_store_sk int8 NULL,
sr_reason_sk int8 NULL,
sr_ticket_number int8 NULL,
sr_return_quantity int4 NULL,
sr_return_amt numeric(7, 2) NULL,
sr_return_tax numeric(7, 2) NULL,
sr_return_amt_inc_tax numeric(7, 2) NULL,
sr_fee numeric(7, 2) NULL,
sr_return_ship_cost numeric(7, 2) NULL,
sr_refunded_cash numeric(7, 2) NULL,
sr_reversed_charge numeric(7, 2) NULL,
sr_store_credit numeric(7, 2) NULL,
sr_net_loss numeric(7, 2) NULL
);
