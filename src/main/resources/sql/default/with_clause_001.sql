--! 所有以"--!" 顶格的行, 在读取后会被移除, 见 com.deepexi.ds.ast.utils.ResUtils::NOT_COMMENT
--! 查看该文件在哪里使用: com.deepexi.ds.ast.utils.SqlTemplateId
--! sql用途: 解析Model节点时 生成整体sql
--! sql参数:
--!    source_alias: 生成的cte别名
--!    source_sql: cte 子查询语句
--!    col_list: 字段列表, 如： colA, colB as xx, cast(colC as int) as yy

with ${source_alias} as ( ${source_sql} )
select ${col_list}
from ${source_alias}