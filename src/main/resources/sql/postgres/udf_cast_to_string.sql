--! ============ 通用规范 =================
--! 以"--!" 顶格的行, 会被移除, 见 com.deepexi.ds.ast.utils.ResUtils::NOT_COMMENT
--! 查看该文件在哪里使用: com.deepexi.ds.ast.utils.SqlTemplateId; 或: 直接看文件名, 到SqlGenerator类中找
--! sql文件命名规范: {该sql所处node名}_{数字编号}.sql, 如 metric_bind_query_001.sql表示 MetricBindQuery中使用该文件
--! 占位符方式: ${变量名}, 如 ${aliasSql}
--! ============= 以下用于描述 sql模板 ======
--! sql用途: 解析Model节点时 生成整体sql
--! sql变量:
--!     colName
cast(${colName} AS text)