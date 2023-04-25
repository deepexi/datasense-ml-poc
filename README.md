# model + metrics + query = sql

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

