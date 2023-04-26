# model + metrics + query => sql
- 计算订单中, 截止12月31日, 每天销售额和门店名
```sql
with dwd_order as (
    select
      ods_order.id          as order_id,
      ods_order.amount      as amount,
      ods_order.created_at  as created_at,
      ods_shop.shop_name    as order_id
    from ods_order 
        inner join ods_shop on ods_order.shop_id = ods_shop.id
),
day_amount as (
select
    shop_name,
    sum(amount) as day_amount,
    EXTRACT(day FROM created_at) day,
from dwd_order
    where created_at > '2020-12-31'
    group by shop_name, EXTRACT(day FROM created_at);
)
select * from day_amount;
```

- model: 可以看成一张大宽表
  - 这张大宽表可以由很多小表(如事实表/维度表) 进行 join得到
  - 例子中的 dwd_order, ods_order, ods_shop

- metric: 可以看成基于这张大宽表进行的聚合
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
- 关于dimension

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

# 扩展规范
- 定义一个 Expression子类, 如 MyExpression extends com.deepexi.ds.ast.expression.Expression
- 在 com.deepexi.ds.ast.visitor.generator.SqlGenerator中实现 visitMyExpression(MyExpression node, Context ctx)
  - 这里可以实现对自定义表达式的 特定解析, 生成 特定的 sql
- 需要的 sql 片段 放到 `res/{dialect}/{sql_id}.sql`
