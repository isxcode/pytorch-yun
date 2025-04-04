#
# Quartz seems to work best with the driver mm.mysql-2.0.7-bin.jar
#
# PLEASE consider using mysql with innodb tables to avoid locking issues
#
# In your Quartz properties file, you'll need to set
# org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.StdJDBCDelegate
#

DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;
DROP TABLE IF EXISTS QRTZ_LOCKS;
DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_SIMPROP_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_JOB_DETAILS;
DROP TABLE IF EXISTS QRTZ_CALENDARS;


CREATE TABLE QRTZ_JOB_DETAILS
(
  SCHED_NAME        VARCHAR(120) NOT NULL,
  JOB_NAME          VARCHAR(200) NOT NULL,
  JOB_GROUP         VARCHAR(200) NOT NULL,
  DESCRIPTION       VARCHAR(250) NULL,
  JOB_CLASS_NAME    VARCHAR(250) NOT NULL,
  IS_DURABLE        VARCHAR(1)   NOT NULL,
  IS_NONCONCURRENT  VARCHAR(1)   NOT NULL,
  IS_UPDATE_DATA    VARCHAR(1)   NOT NULL,
  REQUESTS_RECOVERY VARCHAR(1)   NOT NULL,
  JOB_DATA          BLOB         NULL,
  PRIMARY KEY (SCHED_NAME, JOB_NAME, JOB_GROUP)
);

CREATE TABLE QRTZ_TRIGGERS
(
  SCHED_NAME     VARCHAR(120) NOT NULL,
  TRIGGER_NAME   VARCHAR(200) NOT NULL,
  TRIGGER_GROUP  VARCHAR(200) NOT NULL,
  JOB_NAME       VARCHAR(200) NOT NULL,
  JOB_GROUP      VARCHAR(200) NOT NULL,
  DESCRIPTION    VARCHAR(250) NULL,
  NEXT_FIRE_TIME BIGINT(13)   NULL,
  PREV_FIRE_TIME BIGINT(13)   NULL,
  PRIORITY       INTEGER      NULL,
  TRIGGER_STATE  VARCHAR(16)  NOT NULL,
  TRIGGER_TYPE   VARCHAR(8)   NOT NULL,
  START_TIME     BIGINT(13)   NOT NULL,
  END_TIME       BIGINT(13)   NULL,
  CALENDAR_NAME  VARCHAR(200) NULL,
  MISFIRE_INSTR  SMALLINT(2)  NULL,
  JOB_DATA       BLOB         NULL,
  PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME, JOB_NAME, JOB_GROUP)
    REFERENCES QRTZ_JOB_DETAILS (SCHED_NAME, JOB_NAME, JOB_GROUP)
);

CREATE TABLE QRTZ_SIMPLE_TRIGGERS
(
  SCHED_NAME      VARCHAR(120) NOT NULL,
  TRIGGER_NAME    VARCHAR(200) NOT NULL,
  TRIGGER_GROUP   VARCHAR(200) NOT NULL,
  REPEAT_COUNT    BIGINT(7)    NOT NULL,
  REPEAT_INTERVAL BIGINT(12)   NOT NULL,
  TIMES_TRIGGERED BIGINT(10)   NOT NULL,
  PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CRON_TRIGGERS
(
  SCHED_NAME      VARCHAR(120) NOT NULL,
  TRIGGER_NAME    VARCHAR(200) NOT NULL,
  TRIGGER_GROUP   VARCHAR(200) NOT NULL,
  CRON_EXPRESSION VARCHAR(200) NOT NULL,
  TIME_ZONE_ID    VARCHAR(80),
  PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

CREATE TABLE QRTZ_SIMPROP_TRIGGERS
(
  SCHED_NAME    VARCHAR(120)   NOT NULL,
  TRIGGER_NAME  VARCHAR(200)   NOT NULL,
  TRIGGER_GROUP VARCHAR(200)   NOT NULL,
  STR_PROP_1    VARCHAR(512)   NULL,
  STR_PROP_2    VARCHAR(512)   NULL,
  STR_PROP_3    VARCHAR(512)   NULL,
  INT_PROP_1    INT            NULL,
  INT_PROP_2    INT            NULL,
  LONG_PROP_1   BIGINT         NULL,
  LONG_PROP_2   BIGINT         NULL,
  DEC_PROP_1    NUMERIC(13, 4) NULL,
  DEC_PROP_2    NUMERIC(13, 4) NULL,
  BOOL_PROP_1   VARCHAR(1)     NULL,
  BOOL_PROP_2   VARCHAR(1)     NULL,
  PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

CREATE TABLE QRTZ_BLOB_TRIGGERS
(
  SCHED_NAME    VARCHAR(120) NOT NULL,
  TRIGGER_NAME  VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  BLOB_DATA     BLOB         NULL,
  PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CALENDARS
(
  SCHED_NAME    VARCHAR(120) NOT NULL,
  CALENDAR_NAME VARCHAR(200) NOT NULL,
  CALENDAR      BLOB         NOT NULL,
  PRIMARY KEY (SCHED_NAME, CALENDAR_NAME)
);

CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS
(
  SCHED_NAME    VARCHAR(120) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  PRIMARY KEY (SCHED_NAME, TRIGGER_GROUP)
);

CREATE TABLE QRTZ_FIRED_TRIGGERS
(
  SCHED_NAME        VARCHAR(120) NOT NULL,
  ENTRY_ID          VARCHAR(95)  NOT NULL,
  TRIGGER_NAME      VARCHAR(200) NOT NULL,
  TRIGGER_GROUP     VARCHAR(200) NOT NULL,
  INSTANCE_NAME     VARCHAR(200) NOT NULL,
  FIRED_TIME        BIGINT(13)   NOT NULL,
  SCHED_TIME        BIGINT(13)   NOT NULL,
  PRIORITY          INTEGER      NOT NULL,
  STATE             VARCHAR(16)  NOT NULL,
  JOB_NAME          VARCHAR(200) NULL,
  JOB_GROUP         VARCHAR(200) NULL,
  IS_NONCONCURRENT  VARCHAR(1)   NULL,
  REQUESTS_RECOVERY VARCHAR(1)   NULL,
  PRIMARY KEY (SCHED_NAME, ENTRY_ID)
);

CREATE TABLE QRTZ_SCHEDULER_STATE
(
  SCHED_NAME        VARCHAR(120) NOT NULL,
  INSTANCE_NAME     VARCHAR(200) NOT NULL,
  LAST_CHECKIN_TIME BIGINT(13)   NOT NULL,
  CHECKIN_INTERVAL  BIGINT(13)   NOT NULL,
  PRIMARY KEY (SCHED_NAME, INSTANCE_NAME)
);

CREATE TABLE QRTZ_LOCKS
(
  SCHED_NAME VARCHAR(120) NOT NULL,
  LOCK_NAME  VARCHAR(40)  NOT NULL,
  PRIMARY KEY (SCHED_NAME, LOCK_NAME)
);

commit;

-- 租户表
create table if not exists PY_TENANT
(
  id                      varchar(200)  not null unique primary key comment '租户唯一id',
  name                    varchar(200)  not null comment '租户名称',
  used_member_num         int           not null comment '已使用成员数',
  max_member_num          int           not null comment '最大成员数',
  used_workflow_num       int           not null comment '已使用作业流数',
  max_workflow_num        int           not null comment '最大作业流数',
  status                  varchar(200)  not null comment '租户状态',
  introduce               varchar(500) comment '租户简介',
  remark                  varchar(500) comment '租户描述',
  check_date_time         datetime      not null comment '检测时间',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除'
);

-- 用户表
create table if not exists PY_USER
(
  id                      varchar(200)  not null unique primary key comment '用户唯一id',
  username                varchar(200)  not null comment '用户名称',
  account                 varchar(200)  not null comment '用户账号',
  passwd                  varchar(200) comment '账号密码',
  phone                   varchar(200) comment '手机号',
  email                   varchar(200) comment '邮箱',
  introduce               varchar(500) comment '简介',
  remark                  varchar(500) comment '描述',
  role_code               varchar(200)  not null comment '角色编码',
  status                  varchar(200)  not null comment '用户状态',
  create_by               varchar(200)  not null comment '创建人',
  current_tenant_id       varchar(200) comment '当前用户使用的租户id',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除'
);

-- 初始化系统管理员
insert into PY_USER (id, username, account, passwd, role_code, status, create_by, create_date_time, last_modified_by,
                     last_modified_date_time, version_number)
values ('admin_id', '系统管理员', 'admin', '', 'ROLE_SYS_ADMIN', 'ENABLE', 'admin_id', now(), 'admin_id', now(),
        0);

-- 租户用户关系表
create table if not exists PY_TENANT_USERS
(
  id                      varchar(200)  not null unique primary key comment '关系唯一id',
  user_id                 varchar(200)  not null comment '用户id',
  tenant_id               varchar(200)  not null comment '租户id',
  role_code               varchar(200)  not null comment '角色编码',
  status                  varchar(200)  not null comment '用户状态',
  remark                  varchar(200) comment '备注',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除'
);

-- 集群表
create table if not exists PY_CLUSTER
(
  id                      varchar(200)  not null unique primary key comment '集群唯一id',
  name                    varchar(200)  not null comment '集群名称',
  remark                  varchar(500) comment '集群描述',
  status                  varchar(200)  not null comment '集群状态',
  check_date_time         datetime      not null comment '检测时间',
  all_node_num            int           not null comment '所有节点',
  active_node_num         int           not null comment '激活节点数',
  all_memory_num          double        not null comment '所有内存',
  used_memory_num         double        not null comment '已使用内存',
  all_storage_num         double        not null comment '所有存储',
  used_storage_num        double        not null comment '已使用存储',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 集群节点表
create table if not exists PY_CLUSTER_NODE
(
  id                      varchar(200)  not null unique primary key comment '集群节点唯一id',
  name                    varchar(200)  not null comment '节点名称',
  remark                  varchar(500) comment '节点描述',
  status                  varchar(200)  not null comment '节点状态',
  check_date_time         datetime      not null comment '检测时间',
  all_memory              double        not null comment '所有内存',
  used_memory             double        not null comment '已使用内存',
  all_storage             double        not null comment '所有存储',
  used_storage            double        not null comment '已使用存储',
  cpu_percent             double        not null comment 'cpu使用占比',
  cluster_id              varchar(200)  not null comment '集群id',
  host                    varchar(200)  not null comment '节点服务器host',
  port                    int           not null comment '节点服务器端口号',
  agent_log               varchar(2000) null comment '代理日志',
  username                varchar(200)  not null comment '节点服务器用户名',
  passwd                  varchar(200)  not null comment '节点服务器',
  agent_home_path         varchar(200)  not null comment '至慧云代理安装目录',
  agent_port              varchar(200)  not null comment '至慧云代理服务端口号',
  hadoop_home_path        varchar(200)  null comment 'hadoop家目录',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 数据源表
create table if not exists PY_DATASOURCE
(
  id                      varchar(200)  not null unique primary key comment '数据源唯一id',
  name                    varchar(200)  not null comment '数据源名称',
  jdbc_url                varchar(500)  not null comment '数据源jdbcUrl',
  remark                  varchar(500) comment '描述',
  status                  varchar(200)  not null comment '状态',
  check_date_time         datetime      not null comment '检测时间',
  username                varchar(200) comment '数据源用户名',
  passwd                  varchar(200) comment '数据源密码',
  connect_log             varchar(2000) null comment '测试连接日志',
  db_type                 varchar(200)  not null comment '数据源类型',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 作业流表
create table if not exists PY_WORKFLOW
(
  id                      varchar(200)  not null unique primary key comment '作业流唯一id',
  name                    varchar(200)  not null comment '作业流名称',
  remark                  varchar(500) comment '作业流描述',
  status                  varchar(200)  not null comment '状态',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 作业表
create table if not exists PY_WORK
(
  id                      varchar(200)  not null unique primary key comment '作业唯一id',
  name                    varchar(200)  not null comment '作业名称',
  remark                  varchar(500) comment '作业描述',
  status                  varchar(200)  not null comment '作业状态',
  work_type               varchar(200)  not null comment '作业类型',
  config_id               varchar(200)  not null comment '作业配置id',
  workflow_id             varchar(200)  not null comment '作业流id',
  version_id              varchar(200)  null comment '作业当前最新版本号',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 作业配置表
create table if not exists PY_WORK_CONFIG
(
  id                      varchar(200)  not null unique primary key comment '作业配置唯一id',
  datasource_id           varchar(200) comment '数据源id',
  cluster_id              varchar(200) comment '集群id',
  spark_config            text comment 'spark的作业配置',
  sql_script              text comment 'sql脚本',
  corn                    varchar(200) comment '定时表达式',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 许可证表
create table if not exists PY_LICENSE
(
  id                      varchar(200)  not null unique primary key comment '许可证唯一id',
  code                    varchar(200)  not null comment '许可证编号',
  company_name            varchar(200)  not null comment '公司名称',
  logo                    varchar(2000) not null comment '公司logo',
  remark                  varchar(2000) comment '许可证备注',
  issuer                  varchar(200)  not null comment '许可证签发人',
  start_date_time         datetime      not null comment '许可证起始时间',
  end_date_time           datetime      not null comment '许可证到期时间',
  max_tenant_num          int comment '最大租户数',
  max_member_num          int comment '最大成员数',
  max_workflow_num        int comment '最大作业流数',
  status                  varchar(200)  not null comment '证书状态',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除'
);

-- 自定义API表
create table if not exists PY_API
(
  id                      varchar(200)  not null unique primary key comment '唯一API的id',
  name                    varchar(200)  not null comment 'API名称',
  path                    varchar(200)  not null comment 'API访问地址',
  api_type                varchar(200)  not null comment 'API类型',
  remark                  varchar(2000) comment 'API备注',
  req_header              varchar(2000) comment '请求头',
  req_body                varchar(2000) comment '请求体',
  api_sql                 varchar(2000) not null comment '执行的sql',
  res_body                varchar(2000) not null comment '响应体',
  datasource_id           varchar(200)  not null comment '数据源id',
  status                  varchar(200)  not null comment 'API状态',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 作业配置版本表
create table if not exists PY_WORK_VERSION
(
  id                      varchar(200)  not null unique primary key comment '版本唯一id',
  work_id                 varchar(200)  not null comment '作业id',
  work_type               varchar(200)  not null comment '作业类型',
  datasource_id           varchar(200) comment '数据源id',
  cluster_id              varchar(200) comment '集群id',
  sql_script              text comment 'sql脚本',
  spark_config            text comment 'spark的作业配置',
  corn                    varchar(200)  not null comment '定时表达式',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 作业运行实例表
create table if not exists PY_WORK_INSTANCE
(
  id                      varchar(200)  not null unique primary key comment '实例唯一id',
  version_id              varchar(200) comment '实例版本id',
  work_id                 varchar(200) comment '作业id',
  instance_type           varchar(200) comment '实例类型',
  status                  varchar(200) comment '实例状态',
  plan_start_date_time    datetime comment '计划开始时间',
  next_plan_date_time     datetime comment '下一次开始时间',
  exec_start_date_time    datetime comment '执行开始时间',
  exec_end_date_time      datetime comment '执行结束时间',
  submit_log              text comment '提交日志',
  yarn_log                text comment 'yarn日志',
  spark_star_res          varchar(2000) comment 'spark-star插件返回',
  result_data             text comment '结果数据',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 用户行为记录表
create table if not exists PY_USER_ACTION
(
  id               varchar(200) not null unique primary key comment '用户行为唯一id',
  user_id          varchar(200) comment '用户id',
  tenant_id        varchar(200) comment '租户id',
  req_path         varchar(200) comment '请求路径',
  req_method       varchar(200) comment '请求方式',
  req_header       varchar(2000) comment '请求头',
  req_body         text comment '请求体',
  res_body         text comment '响应体',
  start_timestamp  long comment '开始时间戳',
  end_timestamp    long comment '结束时间戳',
  create_by        varchar(200) not null comment '创建人',
  create_date_time datetime     not null comment '创建时间'
);

alter table PY_CLUSTER
  add cluster_type varchar(100) null comment '集群的类型';

update PY_CLUSTER
set cluster_type='yarn'
where 1 = 1;

-- 给作业实例，添加作业流实例id
alter table PY_WORK_INSTANCE
  add workflow_instance_id varchar(100) null comment '工作流实例id';

-- 给作业实例，添加是否被定时器触发过
alter table PY_WORK_INSTANCE
  add quartz_has_run tinyint(1) null comment '是否被定时器触发过';

-- 创建工作流配置表
create table PY_WORKFLOW_CONFIG
(
  id                      varchar(200)  not null comment '作业流配置唯一id'
    primary key,
  web_config              text          null comment '前端配置',
  node_mapping            varchar(2000) null comment '节点映射关系',
  node_list               varchar(2000) null comment '节点列表',
  dag_start_list          varchar(2000) null comment 'DAG开始节点列表',
  dag_end_list            varchar(2000) null comment 'DAG结束节点列表',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id',
  corn                    varchar(300)  null comment '定时时间',
  constraint id
    unique (id)
);

-- 创建工作流实例表
create table PY_WORKFLOW_INSTANCE
(
  id                      varchar(200)  not null comment '实例唯一id'
    primary key,
  version_id              varchar(200)  null comment '实例版本id',
  flow_id                 varchar(200)  null comment '作业流id',
  instance_type           varchar(200)  null comment '实例类型',
  status                  varchar(200)  null comment '实例状态',
  run_log                 text          null comment '作业流运行日志',
  web_config              text          null comment '前端页面配置信息',
  plan_start_date_time    datetime      null comment '计划开始时间',
  next_plan_date_time     datetime      null comment '下一次开始时间',
  exec_start_date_time    datetime      null comment '执行开始时间',
  exec_end_date_time      datetime      null comment '执行结束时间',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id',
  constraint id
    unique (id)
);

-- 创建工作流版本表
create table PY_WORKFLOW_VERSION
(
  id                      varchar(200)  not null comment '工作流版本唯一id'
    primary key,
  name                    varchar(200)  not null comment '作业流版本名称',
  workflow_id             varchar(200)  not null comment '作业流id',
  workflow_type           varchar(200)  null comment '作业类型',
  node_mapping            text          null comment '节点映射',
  node_list               text          null comment '节点',
  dag_start_list          text          null comment '开始节点',
  dag_end_list            text          null comment '结束节点',
  work_version_map        text          null comment '作业版本映射',
  corn                    varchar(200)  not null comment '定时表达式',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id',
  constraint id
    unique (id)
);

-- 工作流添加工作流配置id
alter table PY_WORKFLOW
  add config_id varchar(100) null comment '工作流配置id';

-- 工作流添加工作流当前版本id
alter table PY_WORKFLOW
  add version_id varchar(100) null comment '版本id';

-- 工作流添加工作流类型
alter table PY_WORKFLOW
  add type varchar(200) null comment '工作流类型';

-- 作业添加置顶标志
alter table PY_WORK
  add top_index int null comment '作业置顶标志';

-- 工作流收藏表
create table PY_WORKFLOW_FAVOUR
(
  id                      varchar(200)  not null comment '工作流收藏唯一id'
    primary key,
  workflow_id             varchar(200)  null comment '工作流唯一id',
  user_id                 varchar(200)  null comment '用户id',
  top_index               int           null comment 'top排序标志',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 自定义锁表
create table PY_LOCKER
(
  id    bigint,
  name  varchar(2000) not null,
  box varchar(2000) null,
  constraint PY_LOCKER_pk
    primary key (id)
);

alter table PY_LOCKER
  modify id int auto_increment;

alter table PY_CLUSTER_NODE
  modify passwd varchar(5000) not null comment '节点服务器密码';

-- 创建资源文件表
create table PY_FILE
(
  id                      varchar(200)  not null comment '文件配置唯一id'
    primary key,
  file_name               varchar(200) null comment '文件名称',
  file_size               varchar(200) null comment '文件大小',
  file_path               varchar(200) null comment '文件存储路径',
  file_type               varchar(200) null comment '文件类型',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id',
  constraint id
    unique (id)
);

-- 添加同步作业的配置
alter table PY_WORK_CONFIG
  add sync_work_config text null comment '同步作业的配置' after corn;

-- 统一脚本内容
alter table PY_WORK_CONFIG
    change sql_script script text null comment '统一脚本内容，包括sql、bash、python脚本';

-- 将cluster_id和spark_config合并成cluster_config
alter table PY_WORK_CONFIG
    drop column cluster_id;

alter table PY_WORK_CONFIG
    drop column spark_config;

alter table PY_WORK_CONFIG
    add cluster_config text null comment '计算集群配置' after sync_work_config;

-- 将cron扩展成cron_config
alter table PY_WORK_CONFIG
    change corn cron_config text null comment '定时表达式';

-- 添加数据同步规则
alter table PY_WORK_CONFIG
    add sync_rule text null comment '数据同步规则' after cluster_config;

-- 添加spark_home_path
alter table PY_CLUSTER_NODE
    add spark_home_path varchar(200) null comment 'standalone模式spark的安装目录' after hadoop_home_path;

-- 添加默认集群
alter table PY_CLUSTER
    add default_cluster boolean null default false comment '默认计算集群' after used_storage_num;

-- 作业运行实例中，添加作业运行的pid
alter table PY_WORK_INSTANCE
    add work_pid varchar(100) null comment '作业运行的进程pid' after result_data;

-- hive数据源，添加hive.metastore.uris配置项
alter table PY_DATASOURCE
    add metastore_uris varchar(200) null comment 'hive数据源 hive.metastore.uris 配置' after db_type;

-- 数据源支持驱动添加
alter table PY_DATASOURCE
    add driver_id varchar(100) not null comment '数据库驱动id' after metastore_uris;

-- 新增数据源驱动表
create table PY_DATABASE_DRIVER
(
    id                      varchar(200)  not null comment '数据源驱动唯一id'
        primary key,
    name                    varchar(200)  not null comment '数据源驱动名称',
    db_type                 varchar(200)  not null comment '数据源类型',
    file_name               varchar(500)  not null comment '驱动名称',
    remark                  varchar(500)  null comment '描述',
    driver_type             varchar(200)  not null comment '驱动类型',
    is_default_driver       boolean       not null comment '是否为默认驱动',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    version_number          int           not null comment '版本号',
    deleted                 int default 0 not null comment '逻辑删除',
    tenant_id               varchar(200)  not null comment '租户id'
);

-- 初始化系统驱动
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('clickhouse_0.5.0', 'clickhouse_0.5.0', 'CLICKHOUSE', 'clickhouse-jdbc-0.5.0.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', 1);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('db2_11.5.8.0', 'db2_11.5.8.0', 'DB2', 'jcc-11.5.8.0.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', 1);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('dm_8.1.1.49', 'dm_8.1.1.49', 'DM', 'Dm8JdbcDriver18-8.1.1.49.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', 1);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('doris_mysql_5.1.49', 'doris_mysql_5.1.49', 'DORIS', 'mysql-connector-java-5.1.49.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', 1);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('hana_2.18.13', 'hana_2.18.13', 'HANA_SAP', 'ngdbc-2.18.13.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', 1);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('hive_uber_2.6.3.jar', 'hive_uber_2.6.3.jar', 'HIVE', 'hive-jdbc-uber-2.6.3.0-235.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', 1);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('hive_3.1.3', 'hive_3.1.3', 'HIVE', 'hive-jdbc-3.1.3-standalone.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', 0);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('mysql_5.1.49', 'mysql_5.1.49', 'MYSQL', 'mysql-connector-java-5.1.49.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', 0);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('mysql_8.1.0', 'mysql_8.1.0', 'MYSQL', 'mysql-connector-j-8.1.0.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', 1);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('oceanbase_2.4.6', 'oceanbase_2.4.6', 'OCEANBASE', 'oceanbase-client-2.4.6.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', 1);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('oracle_19.20.0.0', 'oracle_19.20.0.0', 'ORACLE', 'ojdbc10-19.20.0.0.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', 1);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('postgre_42.6.0', 'postgre_42.6.0', 'POSTGRE_SQL', 'postgresql-42.6.0.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', 1);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('sqlServer_12.4.2.jre8', 'sqlServer_12.4.2.jre8', 'SQL_SERVER', 'mssql-jdbc-12.4.2.jre8.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', 1);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('star_rocks(mysql_5.1.49)', 'star_rocks(mysql_5.1.49)', 'STAR_ROCKS', 'mysql-connector-java-5.1.49.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', 1);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('tidb(mysql_5.1.49)', 'tidb(mysql_5.1.49)', 'TIDB', 'mysql-connector-java-5.1.49.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', 1);

-- 工作流添加默认计算引擎配置
alter table PY_WORKFLOW
    add default_cluster_id varchar(200) null comment '默认计算引擎' after status;

-- 数据源日志长度扩大
alter table PY_DATASOURCE
    change connect_log connect_log text null comment '测试连接日志';

-- PY_WORKFLOW_CONFIG将cron扩展成cron_config
alter table PY_WORKFLOW_CONFIG
    change corn cron_config text null comment '定时表达式';

-- PY_WORK_VERSION将cron扩展成cron_config
alter table PY_WORK_VERSION
    change corn cron_config text null comment '定时表达配置';

-- PY_WORK_VERSION将cluster_id扩展成cluster_config
alter table PY_WORK_VERSION
    change cluster_id cluster_config text null comment '集群配置';

-- PY_WORK_VERSION将sql_script扩展成script
alter table PY_WORK_VERSION
    change sql_script script text null comment '脚本内容';

-- 删除PY_WORK_VERSION的spark_config
alter table PY_WORK_VERSION
    drop column spark_config;

-- 添加同步作业的配置
alter table PY_WORK_VERSION
    add sync_work_config text null comment '同步作业的配置' after cron_config;

-- 添加数据同步规则
alter table PY_WORK_VERSION
    add sync_rule text null comment '数据同步规则' after sync_work_config;

-- 修改错别字
alter table PY_WORKFLOW_VERSION
    change corn cron varchar(200) not null comment '定时表达式';

-- 作业流版本里加dag图
alter table PY_WORKFLOW_VERSION
    add web_config text null comment '作业流的dag图' after cron;

-- 作业流实例添加耗时
alter table PY_WORKFLOW_INSTANCE
    add duration int null comment '耗时时间（秒）' after exec_end_date_time;

-- 作业流实例添加耗时
alter table PY_WORK_INSTANCE
    add duration int null comment '耗时时间（秒）' after exec_end_date_time;

-- PY_WORK_VERSION将cron扩展成cron_config
alter table PY_WORKFLOW_VERSION
    change cron cron_config text not null comment '调度配置';

-- 作业添加udf函数开关
ALTER TABLE PY_WORK_CONFIG ADD udf_status BOOL DEFAULT false NULL;
ALTER TABLE PY_WORK_CONFIG CHANGE udf_status udf_status BOOL DEFAULT false NULL AFTER cron_config;

ALTER TABLE PY_WORK_CONFIG ADD jar_conf text NULL;
ALTER TABLE PY_WORK_CONFIG CHANGE jar_conf jar_conf text NULL AFTER sync_rule;

ALTER TABLE PY_WORKFLOW_CONFIG ADD external_call CHAR NULL;
ALTER TABLE PY_WORKFLOW_CONFIG CHANGE external_call external_call CHAR NULL AFTER dag_end_list;
ALTER TABLE PY_WORKFLOW_CONFIG ADD access_key varchar(100) NULL;
ALTER TABLE PY_WORKFLOW_CONFIG CHANGE access_key access_key varchar(100) NULL AFTER external_call;

-- 新增字段 createMode
alter table PY_API
    add token_type varchar(200) null comment '认证方式' after status;

alter table PY_API
    add page_type varchar(50) null comment '分页状态' after token_type;

alter table PY_API
    modify page_type boolean null comment '分页状态';

alter table PY_API
    modify api_sql varchar(2000) null comment '执行的sql';

-- 删除资源文件的路径
alter table PY_FILE drop column FILE_PATH;

-- 添加备注字段
alter table PY_FILE
  add REMARK varchar(500) comment '备注';

-- 添加自定义jar作业配置
alter table PY_WORK_CONFIG
  add JAR_JOB_CONFIG text comment '自定义jar作业配置';

-- 版本添加自定义作业配置
alter table PY_WORK_VERSION
  add JAR_JOB_CONFIG text comment '自定义作业配置';

-- 依赖配置
alter table PY_WORK_CONFIG
  add LIB_CONFIG text comment '作业依赖文件';

-- 自定义配置
alter table PY_WORK_CONFIG
  add FUNC_CONFIG text comment '自定义函数配置';

alter table PY_WORK_VERSION
  add LIB_CONFIG text comment '依赖配置';

alter table PY_WORK_VERSION
  add FUNC_CONFIG text comment '自定义函数配置';

-- 作业支持容器sql
alter table PY_WORK_CONFIG
    add CONTAINER_ID varchar(200);

-- 作业版本支持容器sql
alter table PY_WORK_VERSION
    add CONTAINER_ID varchar(200);

-- 添加接口调用作业配置
alter table PY_WORK_CONFIG
  add API_WORK_CONFIG text null comment '接口调用作业的配置';

-- 版本中添加接口调用作业配置
alter table PY_WORK_VERSION
  add API_WORK_CONFIG text null comment '接口调用作业的配置';

-- 新增kafka数据源配置
alter table PY_DATASOURCE
  add KAFKA_CONFIG text null comment 'kafka数据源配置';

-- 新增kafka驱动
INSERT INTO PY_DATABASE_DRIVER (ID, NAME, DB_TYPE, FILE_NAME, DRIVER_TYPE, IS_DEFAULT_DRIVER, REMARK, CREATE_BY, CREATE_DATE_TIME, LAST_MODIFIED_BY, LAST_MODIFIED_DATE_TIME, VERSION_NUMBER, DELETED, TENANT_ID)
VALUES ('kafka_client_3.1.2', 'kafka_client_3.1.2', 'KAFKA', 'kafka_client_3.1.2.jar', 'SYSTEM_DRIVER', true, '系统自带驱动', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun');

-- 添加监控表
create table PY_MONITOR
(
  id                      varchar(200)  not null comment '分享表单链接id'
    primary key,
  cluster_id              varchar(200)  not null comment '集群id',
  cluster_node_id         varchar(200)  not null comment '集群节点id',
  status                  varchar(100)  not null comment '监控状态',
  log                     text          not null comment '日志',
  used_storage_size       long null comment '已使用存储',
  used_memory_size        long null comment '已使用内存',
  network_io_read_speed   long null comment '网络读速度',
  network_io_write_speed  long null comment '网络写速度',
  disk_io_read_speed      long null comment '磁盘读速度',
  disk_io_write_speed     long null comment '磁盘写速度',
  cpu_percent
    double null comment 'cpu占用',
  create_date_time        datetime      not null comment '创建时间',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- standalone集群节点支持安装spark-local组件
alter table PY_CLUSTER_NODE
  add INSTALL_SPARK_LOCAL bool default false null comment '是否安装spark-local组件';

-- 将ojdbc10-19.20.0.0.jar更新为ojdbc8-19.23.0.0.jar
UPDATE PY_DATABASE_DRIVER SET ID = 'ojdbc8-19.23.0.0', NAME = 'ojdbc8-19.23.0.0', FILE_NAME = 'ojdbc8-19.23.0.0.jar' WHERE ID LIKE 'oracle#_19.20.0.0';

-- 添加下次执行时间
alter table PY_WORKFLOW
  add NEXT_DATE_TIME datetime comment '是否安装spark-local组件';

-- 添加消息体表
create table PY_MESSAGE
(
  id                      varchar(200)  not null comment '消息消息体id'
    primary key,
  name                    varchar(200)  not null comment '消息体名称',
  status                  varchar(100)  not null comment '消息体状态',
  remark                  varchar(500) comment '消息体备注',
  msg_type                varchar(200)  not null comment '消息体类型，邮箱/阿里短信/飞书',
  msg_config              text          not null comment '消息体配置信息',
  response                text comment '检测响应',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 添加告警表
create table PY_ALARM
(
  id                      varchar(200)  not null comment '告警id'
    primary key,
  name                    varchar(200)  not null comment '告警名称',
  status                  varchar(100)  not null comment '消息状态，启动/停止',
  remark                  varchar(500) comment '告警备注',
  alarm_type              varchar(200)  not null comment '告警类型，作业/作业流',
  alarm_event             varchar(200)  not null comment '告警的事件，开始运行/运行失败/运行结束',
  msg_id                  varchar(100)  not null comment '使用的消息体',
  alarm_template          text          not null comment '告警的模版',
  receiver_list           text          null comment '告警接受者',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 添加消息告警实例表
create table PY_ALARM_INSTANCE
(
  id               varchar(200)  not null comment '告警实例id'
    primary key,
  alarm_id         varchar(200)  not null comment '告警id',
  send_status      varchar(100)  not null comment '是否发送成功',
  alarm_type       varchar(200)  not null comment '告警类型，作业/作业流',
  alarm_event      varchar(200)  not null comment '触发的事件',
  msg_id           varchar(100)  not null comment '告警的消息体',
  content          text          not null comment '发送消息的内容',
  response         text          not null comment '事件响应',
  instance_id      varchar(200)  not null comment '任务实例id',
  receiver         varchar(200)  not null comment '消息接受者',
  send_date_time   datetime      not null comment '发送的时间',
  create_date_time datetime      not null comment '创建时间',
  deleted          int default 0 not null comment '逻辑删除',
  tenant_id        varchar(200)  not null comment '租户id'
);

-- 作业添加基线
alter table PY_WORK_CONFIG
  add ALARM_LIST text comment '绑定的基线';

-- 作业流添加基线
alter table PY_WORKFLOW_CONFIG
  add ALARM_LIST text comment '绑定的基线';

-- 作业添加基线
alter table PY_WORK_VERSION
  add ALARM_LIST text comment '绑定的基线';

-- 作业流添加基线
alter table PY_WORKFLOW_VERSION
  add ALARM_LIST text comment '绑定的基线';

-- 添加外部调用开关状态
alter table PY_WORKFLOW_CONFIG
  add INVOKE_STATUS varchar(100) default 'OFF' not null comment '是否启动外部调用';

-- 新增excel同步配置字段
ALTER TABLE PY_WORK_CONFIG
    ADD EXCEL_SYNC_CONFIG LONGTEXT;

-- 实例表中添加EXCEL_SYNC_CONFIG字段
ALTER TABLE PY_WORK_VERSION
    ADD EXCEL_SYNC_CONFIG LONGTEXT;

-- 修复作业调度信息无法配置分钟级别配置
alter table PY_WORK_CONFIG
    modify cron_config varchar(2000) null comment '定时表达式';

-- 修复作业调度信息无法配置分钟级别配置
alter table PY_WORKFLOW_VERSION
    modify cron_config varchar(2000) null comment '定时表达式';

-- 添加外部调用url
ALTER TABLE PY_WORKFLOW_CONFIG
    ADD INVOKE_URL LONGTEXT;