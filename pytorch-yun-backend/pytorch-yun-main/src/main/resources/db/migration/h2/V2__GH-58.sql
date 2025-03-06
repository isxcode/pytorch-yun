 -- 智能体表
create table if not exists PY_AI
(
    id                      varchar(200)  not null unique primary key comment '智能体唯一id',
    name                    varchar(200)  not null comment '智能体名称',
    remark                  varchar(500) comment '智能体描述',
    status                  varchar(200)  not null comment '智能体状态',
    check_date_time         datetime      not null comment '智能体检测时间',
    ai_type                 varchar(200)  not null comment '智能体类型',
    ai_log                  varchar(2000) null comment '智能体部署日志',
    auth_config varchar(2000) null comment '智能体部署日志',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    deleted                 int default 0 not null comment '逻辑删除',
    tenant_id               varchar(200)  not null comment '租户id'
);