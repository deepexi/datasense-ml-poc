# 这里描述 语义层面的udf

# 使用语法
- udf_function({udf_name}, arg1, arg2, ... argN)


# udf
- create_date_by_ymd(year::int, month::int, day::int)
  - 如: udf_function(create_date_by_ymd, date_dim.year, date_dim.moy, date_dim.dom)

- cast(colName, fromType, toType, arg0, arg1, ....)
  - 如 udf_function(cast, date_dim.year, 'int', 'string')
  - 如 udf_function(cast, date_dim.date, 'date', 'string', '%Y-%m-%d %H:%M:%S')
  - 如 udf_function(cast, date_dim.date_str, 'string', 'date', '%Y-%m-%d %H:%M:%S')
