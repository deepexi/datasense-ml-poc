# doc

- Ast
- 核心类 ModelVisitor. 不同的 ModelVisitor 对树进行一次遍历, 得到自己想要的结果
  - ScopeCollector + ScopeCollectorContext
    - 收集所有可访问的 table / model, 为后续处理准备

  - ScopeBinder + ScopeBinderContext
    - 记录每个节点能够访问的哪些table, 并进行绑定
    - 比如 columns 下有个字段: expr = tableA.colB, 此时这个 expression 就需要能够访问 tableA
    - 如果上下文没有提供 tableA给此节点(tableA 不在当前source, 也不在当前joins中, 以及别名)
    - 则有语义错误

  - 生成sql => ModelSqlGenerator
