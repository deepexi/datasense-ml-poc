CREATE TABLE public.customer_address (
ca_address_sk int8 NULL,
ca_address_id varchar(16) NULL,
ca_street_number varchar(10) NULL,
ca_street_name varchar(60) NULL,
ca_street_type varchar(15) NULL,
ca_suite_number varchar(10) NULL,
ca_city varchar(60) NULL,
ca_county varchar(30) NULL,
ca_state varchar(2) NULL,
ca_zip varchar(10) NULL,
ca_country varchar(20) NULL,
ca_gmt_offset numeric(5, 2) NULL,
ca_location_type varchar(20) NULL
);
