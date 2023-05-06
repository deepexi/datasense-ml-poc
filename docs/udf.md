# 这里描述 语义层面的udf

# 使用语法
- udf_function({udf_name}, arg1, arg2, ... argN)


# udf
- create_date_by_ymd(year::int, month::int, day::int)
  - 如: udf_function(create_date_by_ymd, date_dim.year, date_dim.moy, date_dim.dom)

- cast(colName, toType, arg0, arg1, ....)
  - 如 udf_function(cast, date_dim.year, string)
  - 如 udf_function(cast, date_dim.date, string, %Y-%m-%d %H:%M:%S)
  - 如 udf_function(cast, date_dim.date_str, date, %Y-%m-%d %H:%M:%S)

- date_diff(day|month|year, date0, date1|base_date)
  - base_date 是可以看做一个常量日期使用, 目前设置为 1970-01-01
  - udf_function(date_diff, day, created_at, base_date)