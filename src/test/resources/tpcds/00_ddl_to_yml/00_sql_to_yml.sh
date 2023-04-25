#!/usr/bin/env bash

#####################
# 将 .sql 文件处理成 ModelML.yml文件
# 要求:
#  1. 以 .sql 为后缀名
#  2. 没有空行
#  3. 每个 column 单独一行
# 执行之后, 会生成一个 01_base_table 文件夹, 里面放着对应的 yml
#####################

cd `dirname $0`

output_dir=01_base_table
rm -rf $output_dir
mkdir $output_dir

header="version: v1
resource: model_ml
name: _name_
source:
  source_type: table
  table: _name_
  datasource: ds_id_ck01
joins:
dimensions:
columns:"

column_pattern="  - name: _name_
    data_type: _type_"

type_convert() {
  in_type="$1"
#  echo "in_type=$in_type" >&2
  if    [[ $in_type = varchar* ]];    then echo "string";
  elif  [[ $in_type = int* ]];        then echo "int";
  elif  [[ $in_type = date ]];        then echo "date";
  elif  [[ $in_type = numeric* ]];    then echo "decimal";
#  elif  [[ $in_type = int* ]];        then echo "int";
  else
    echo "unknown type=$in_type" >&2
    exit 1
  fi
}

ls | grep ".sql$" | while read in_file; do
  echo "process $in_file"

  # first in_file should be create table public.xxx ....
  table_name=`head -n1 $in_file | cut -d' ' -f3 | cut -d'.' -f2`
  # echo $table_name
  out_file="${output_dir}/${table_name}.yml"
  # echo "$header" | sed "s/_name_/$table_name/g"
  echo "$header" | sed "s/_name_/$table_name/g" >> ${out_file}

  # remove first and last in_file, then process each column
  tail -n +2 $in_file | head -n -1 | while read line; do
    # echo "Processing line: $line"
    col_name=`echo $line | cut -d' ' -f1`
    in_type=`echo $line | cut -d' ' -f2`
    out_type=`type_convert $in_type`
    echo "$column_pattern" | sed "s/_name_/$col_name/g" \
                           | sed "s/_type_/$out_type/g" >> $out_file
  done
done

