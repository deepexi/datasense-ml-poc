CREATE TABLE public.call_center (
cc_call_center_sk int8 NULL,
cc_call_center_id varchar(16) NULL,
cc_rec_start_date date NULL,
cc_rec_end_date date NULL,
cc_closed_date_sk int4 NULL,
cc_open_date_sk int4 NULL,
cc_name varchar(50) NULL,
cc_class varchar(50) NULL,
cc_employees int4 NULL,
cc_sq_ft int4 NULL,
cc_hours varchar(20) NULL,
cc_manager varchar(40) NULL,
cc_mkt_id int4 NULL,
cc_mkt_class varchar(50) NULL,
cc_mkt_desc varchar(100) NULL,
cc_market_manager varchar(40) NULL,
cc_division int4 NULL,
cc_division_name varchar(50) NULL,
cc_company int4 NULL,
cc_company_name varchar(50) NULL,
cc_street_number varchar(10) NULL,
cc_street_name varchar(60) NULL,
cc_street_type varchar(15) NULL,
cc_suite_number varchar(10) NULL,
cc_city varchar(60) NULL,
cc_county varchar(30) NULL,
cc_state varchar(2) NULL,
cc_zip varchar(10) NULL,
cc_country varchar(20) NULL,
cc_gmt_offset numeric(5, 2) NULL,
cc_tax_percentage numeric(5, 2) NULL
);
