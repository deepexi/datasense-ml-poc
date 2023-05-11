-- 累计三天销售额
-- 指标定义 sum(ss_net_paid), window: 3 day
-- 派生指标: 日期=2000年

-- base_model = store_sales join date_dim, 保留 ss_net_paid, d_date
-- metric_def 指定 sum(ss_net_paid), window = trailing 3 day
-- metric_def 指定 sum_over(ss_net_paid, 3, day)
-- query_def 指定 时间=2000年



-- 基础指标实现1:
-- sum + window 实现 = 先group by, 再 window
with
-- base_model
base_model as (
    select
        dd.d_date,
        ss.ss_net_paid
    from store_sales ss
             inner join date_dim dd  on ss.ss_sold_date_sk = dd.d_date_sk
    where ss.ss_net_paid is not null
)

-- 第一个指标计算
, three_day_paid as (
    -- sum + window的处理方式
    -- step1: group by
    with day_paid as (
        select d_date,
        sum(ss_net_paid) as day_paid
        from base_model
        group by d_date
    )
    -- step2: window, 对于支持 range的dbms 才可以使用
    select
        d_date,
        day_paid,
        sum(day_paid) over (
            order by d_date range between '2 day' preceding and current row -- 需要支持 range
        ) as three_day_paid
    from day_paid
)
-- 派生指标计算规则, 只添加维度过滤条件
select
    d_date,
    three_day_paid
from three_day_paid
where extract(year from d_date) = 2000
order by d_date
limit 1000
;


-- 基础指标实现2:
-- sum + self_join = 先group by, 再 self_join 通用方案
with
-- 大宽表
base_model as (
    select
        dd.d_date,
        ss.ss_net_paid
    from store_sales ss
             inner join date_dim dd  on ss.ss_sold_date_sk = dd.d_date_sk
    where ss.ss_net_paid is not null
)

-- 第一个指标计算
, three_day_paid as (
    -- sum + self_join
    -- step1: group by
    with day_paid as (
        select d_date,
               sum(ss_net_paid) as day_paid
        from base_model
        group by d_date
    )
    -- step2: self_join
    , self_join as (
        select
            s1.d_date,
            s2.day_paid
        from day_paid s1 inner join day_paid s2
            on s1.d_date >= s2.d_date and s1.d_date <= s2.d_date + 2
    )
    -- step3: sum
    select
        d_date,
        sum(day_paid) as three_day_paid
    from self_join
    group by d_date
)
-- 派生指标计算规则, 只添加维度过滤条件
select
    d_date,
    three_day_paid
from three_day_paid
where extract(year from d_date) = 2000
order by d_date
limit 1000
;













