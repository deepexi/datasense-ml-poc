# model + metrics + query => sql

- 计算订单中, 截止12月31日, 每天销售额和门店名

```sql
with dwd_order as (
    select ods_order.id        as order_id,
          ods_order.amount     as amount,
          ods_order.created_at as created_at,
          ods_shop.shop_name   as order_id
   from ods_order
        inner join ods_shop on ods_order.shop_id = ods_shop.id
)
select shop_name,
       sum(amount) as day_amount,
       EXTRACT(day FROM created_at) day,
from dwd_order
where created_at > '2020-12-31'
group by shop_name, EXTRACT(day FROM created_at);
```

- model: 可以看成一张大宽表
    - 这张大宽表可以由很多小表(如事实表/维度表) 进行 join得到
    - 例子中的 dwd_order, ods_order, ods_shop

- metric: 可以看成基于这张大宽表进行的聚合, 对一个特定列的聚合操作
    - sum(amount) as day_amount, day_amount 就是一个指标, 其计算逻辑是 sum(amount)

- metric_query: 针对 metric的一些更细致的描述条件
    - 计算时考虑的维度 group by 中的条件, 如 group by model.shop_name, model.product_id
    - 窗口函数(可选), 如 window(3 days include today). 这里是语义层面的描述, 需要转换成sql
    - 针对model的过滤条件(可选), 如 model.colA = 'x'
    - 针对指标的过滤条件, 如 total_profit > 1000
    - 上文例子中的:
        - created_at > '2020-12-31'
        - group by shop_name, EXTRACT(day FROM created_at);

# specification v0.1

- 基本格式

```
version: v1
resource: model_ml | metrics_ml | metrics_query
name: xxx
其他字段: xxx
```

- name 全局唯一, 因为需要被引用

## model

- model 用于描述一个 Model
- Model对外: 大宽表
- 用于描述模型
- 不允许在 model 中进行
    - group by
    - 不进行 filter, 因为这张 "大宽表" 是给后续操作提供资源, 无需在这一层进行 filter
- 支持的操作
    - join
        - join时候的条件约束未 tableA.colA = tableB.colB. 因为主要是 fact join dim 模式
    - 是否支持 union
- 支持 Model join Model
- column: 可以是计算列
- 关于dimension
    - 必须是 column的某一列, 如果 dimension需要计算的, 比如 1/0 => 男/女, 可以在 column中增加这样的一列

## metrics

- Metric 描述一段逻辑: 基于单个Model(由ModelML描述), 输入是多个同类型的值, 输出是"单个值"
- 对应 sql中的 聚合函数
- 支持的操作
    - aggregate
- 不支持的操作
    - filter (因为这部分描述计算逻辑, filter描述的是对数据集的处理)

## query

- Metric 描述 分组维度
- 对应 sql中的 group by
- 二级计算对应 窗口函数
- 支持的操作
    - filter: 对最终结果的筛选

# 设计思路

- 第一步, 手写或者工具, 生成一个 Yml文件, 这些yml文件包含
    - 多个model, 这些model形成一个完整的树, 只有一个 root
    - 多个metrics(但是基于同一个model), 依赖的model是上面定义的root
    - 一个 model_query, 里面的metrics 在上面有定义
    - 里面的操作, 是描述性的(与特定sql方言无关)
- 使用上面的yml, 构建一棵 yml tree(字面构建, 不进行校验)
- 使用 ymlTree 构建一棵 AstTree
    - 校验AstTree的完整性, 比如 model是否缺失, 字段是否存在
    - YmlTree 与 AstTree大体相当, 但是不严格相同
- 使用遍历器 对 AstTree进行遍历, 每个节点生成一段 sql片段, 进行组装后得到 整个sql
    - 对于特定语义Expression, 在解析时, 根据sql方言不同进行解析

# 扩展规范

- 在SqlGenerator 中处理所有 sql的生成
- 需要的 sql 片段 放到 `res/{dialect}/{sql_id}.sql`, 如 res/sql/postgres/model_001.sql,
  具体规范见  `res/sql/template.sql`

# 项目结构说明
- 首先要 `mvn clean generate-sources`, 生成一些 java代码

- src/main/java   ====> 常规java目录
- src/main/antlr4 ====> 这里存放 antlr4 生成的代码
- src/test/java   ====> 常规单元测试
- src/test/e2e    ====> 端到端测试, 需要配置一些数据库地址等, 如果这些不配置会测试失败
- src/test/antlr4 ====> antlr4 相关的测试