-- Thanks to Patrick Lightbody for submitting this...
--
-- In your Quartz properties file, you'll need to set
-- org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.PostgreSQLDelegate

DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;
DROP TABLE IF EXISTS QRTZ_LOCKS;
DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_SIMPROP_TRIGGERS;
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
  IS_DURABLE        BOOL         NOT NULL,
  IS_NONCONCURRENT  BOOL         NOT NULL,
  IS_UPDATE_DATA    BOOL         NOT NULL,
  REQUESTS_RECOVERY BOOL         NOT NULL,
  JOB_DATA          BYTEA        NULL,
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
  NEXT_FIRE_TIME BIGINT       NULL,
  PREV_FIRE_TIME BIGINT       NULL,
  PRIORITY       INTEGER      NULL,
  TRIGGER_STATE  VARCHAR(16)  NOT NULL,
  TRIGGER_TYPE   VARCHAR(8)   NOT NULL,
  START_TIME     BIGINT       NOT NULL,
  END_TIME       BIGINT       NULL,
  CALENDAR_NAME  VARCHAR(200) NULL,
  MISFIRE_INSTR  SMALLINT     NULL,
  JOB_DATA       BYTEA        NULL,
  PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME, JOB_NAME, JOB_GROUP)
    REFERENCES QRTZ_JOB_DETAILS (SCHED_NAME, JOB_NAME, JOB_GROUP)
);

CREATE TABLE QRTZ_SIMPLE_TRIGGERS
(
  SCHED_NAME      VARCHAR(120) NOT NULL,
  TRIGGER_NAME    VARCHAR(200) NOT NULL,
  TRIGGER_GROUP   VARCHAR(200) NOT NULL,
  REPEAT_COUNT    BIGINT       NOT NULL,
  REPEAT_INTERVAL BIGINT       NOT NULL,
  TIMES_TRIGGERED BIGINT       NOT NULL,
  PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CRON_TRIGGERS
(
  SCHED_NAME      VARCHAR(120) NOT NULL,
  TRIGGER_NAME    VARCHAR(200) NOT NULL,
  TRIGGER_GROUP   VARCHAR(200) NOT NULL,
  CRON_EXPRESSION VARCHAR(120) NOT NULL,
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
  BOOL_PROP_1   BOOL           NULL,
  BOOL_PROP_2   BOOL           NULL,
  PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

CREATE TABLE QRTZ_BLOB_TRIGGERS
(
  SCHED_NAME    VARCHAR(120) NOT NULL,
  TRIGGER_NAME  VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  BLOB_DATA     BYTEA        NULL,
  PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CALENDARS
(
  SCHED_NAME    VARCHAR(120) NOT NULL,
  CALENDAR_NAME VARCHAR(200) NOT NULL,
  CALENDAR      BYTEA        NOT NULL,
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
  FIRED_TIME        BIGINT       NOT NULL,
  SCHED_TIME        BIGINT       NOT NULL,
  PRIORITY          INTEGER      NOT NULL,
  STATE             VARCHAR(16)  NOT NULL,
  JOB_NAME          VARCHAR(200) NULL,
  JOB_GROUP         VARCHAR(200) NULL,
  IS_NONCONCURRENT  BOOL         NULL,
  REQUESTS_RECOVERY BOOL         NULL,
  PRIMARY KEY (SCHED_NAME, ENTRY_ID)
);

CREATE TABLE QRTZ_SCHEDULER_STATE
(
  SCHED_NAME        VARCHAR(120) NOT NULL,
  INSTANCE_NAME     VARCHAR(200) NOT NULL,
  LAST_CHECKIN_TIME BIGINT       NOT NULL,
  CHECKIN_INTERVAL  BIGINT       NOT NULL,
  PRIMARY KEY (SCHED_NAME, INSTANCE_NAME)
);

CREATE TABLE QRTZ_LOCKS
(
  SCHED_NAME VARCHAR(120) NOT NULL,
  LOCK_NAME  VARCHAR(40)  NOT NULL,
  PRIMARY KEY (SCHED_NAME, LOCK_NAME)
);

CREATE INDEX IDX_QRTZ_J_REQ_RECOVERY
  ON QRTZ_JOB_DETAILS (SCHED_NAME, REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_J_GRP
  ON QRTZ_JOB_DETAILS (SCHED_NAME, JOB_GROUP);

CREATE INDEX IDX_QRTZ_T_J
  ON QRTZ_TRIGGERS (SCHED_NAME, JOB_NAME, JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_JG
  ON QRTZ_TRIGGERS (SCHED_NAME, JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_C
  ON QRTZ_TRIGGERS (SCHED_NAME, CALENDAR_NAME);
CREATE INDEX IDX_QRTZ_T_G
  ON QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_T_STATE
  ON QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_STATE
  ON QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP, TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_G_STATE
  ON QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_GROUP, TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NEXT_FIRE_TIME
  ON QRTZ_TRIGGERS (SCHED_NAME, NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST
  ON QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_STATE, NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_MISFIRE
  ON QRTZ_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE
  ON QRTZ_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE_GRP
  ON QRTZ_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_GROUP, TRIGGER_STATE);

CREATE INDEX IDX_QRTZ_FT_TRIG_INST_NAME
  ON QRTZ_FIRED_TRIGGERS (SCHED_NAME, INSTANCE_NAME);
CREATE INDEX IDX_QRTZ_FT_INST_JOB_REQ_RCVRY
  ON QRTZ_FIRED_TRIGGERS (SCHED_NAME, INSTANCE_NAME, REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_FT_J_G
  ON QRTZ_FIRED_TRIGGERS (SCHED_NAME, JOB_NAME, JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_JG
  ON QRTZ_FIRED_TRIGGERS (SCHED_NAME, JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_T_G
  ON QRTZ_FIRED_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_FT_TG
  ON QRTZ_FIRED_TRIGGERS (SCHED_NAME, TRIGGER_GROUP);

-- 0.0.4
-- 租户表
CREATE TABLE IF NOT EXISTS PY_TENANT
(
  id                      VARCHAR(200)  NOT NULL UNIQUE PRIMARY KEY,
  name                    VARCHAR(200)  NOT NULL,
  used_member_num         INT           NOT NULL,
  max_member_num          INT           NOT NULL,
  used_workflow_num       INT           NOT NULL,
  max_workflow_num        INT           NOT NULL,
  status                  VARCHAR(200)  NOT NULL,
  introduce               VARCHAR(500),
  remark                  VARCHAR(500),
  check_date_time         TIMESTAMP     NOT NULL,
  create_by               VARCHAR(200)  NOT NULL,
  create_date_time        TIMESTAMP     NOT NULL,
  last_modified_by        VARCHAR(200)  NOT NULL,
  last_modified_date_time TIMESTAMP     NOT NULL,
  version_number          INT           NOT NULL,
  deleted                 INT DEFAULT 0 NOT NULL
);

COMMENT ON COLUMN PY_TENANT.id IS '租户唯一id';
COMMENT ON COLUMN PY_TENANT.name IS '租户名称';
COMMENT ON COLUMN PY_TENANT.used_member_num IS '已使用成员数';
COMMENT ON COLUMN PY_TENANT.max_member_num IS '最大成员数';
COMMENT ON COLUMN PY_TENANT.used_workflow_num IS '已使用作业流数';
COMMENT ON COLUMN PY_TENANT.max_workflow_num IS '最大作业流数';
COMMENT ON COLUMN PY_TENANT.status IS '租户状态';
COMMENT ON COLUMN PY_TENANT.introduce IS '租户简介';
COMMENT ON COLUMN PY_TENANT.remark IS '租户描述';
COMMENT ON COLUMN PY_TENANT.check_date_time IS '检测时间';
COMMENT ON COLUMN PY_TENANT.create_by IS '创建人';
COMMENT ON COLUMN PY_TENANT.create_date_time IS '创建时间';
COMMENT ON COLUMN PY_TENANT.last_modified_by IS '更新人';
COMMENT ON COLUMN PY_TENANT.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN PY_TENANT.version_number IS '版本号';
COMMENT ON COLUMN PY_TENANT.deleted IS '逻辑删除';

-- 用户表
CREATE TABLE IF NOT EXISTS PY_USER
(
  id                      VARCHAR(200)      NOT NULL UNIQUE PRIMARY KEY,
  username                VARCHAR(200)      NOT NULL,
  account                 VARCHAR(200)      NOT NULL,
  passwd                  VARCHAR(200),
  phone                   VARCHAR(200),
  email                   VARCHAR(200),
  introduce               VARCHAR(500),
  remark                  VARCHAR(500),
  role_code               VARCHAR(200)      NOT NULL,
  status                  VARCHAR(200)      NOT NULL,
  create_by               VARCHAR(200)      NOT NULL,
  current_tenant_id       VARCHAR(200),
  create_date_time        TIMESTAMP         NOT NULL,
  last_modified_by        VARCHAR(200)      NOT NULL,
  last_modified_date_time TIMESTAMP         NOT NULL,
  version_number          INTEGER           NOT NULL,
  deleted                 INTEGER DEFAULT 0 NOT NULL
);

COMMENT ON TABLE PY_USER IS '用户表';
COMMENT ON COLUMN PY_USER.id IS '用户唯一id';
COMMENT ON COLUMN PY_USER.username IS '用户名称';
COMMENT ON COLUMN PY_USER.account IS '用户账号';
COMMENT ON COLUMN PY_USER.passwd IS '账号密码';
COMMENT ON COLUMN PY_USER.phone IS '手机号';
COMMENT ON COLUMN PY_USER.email IS '邮箱';
COMMENT ON COLUMN PY_USER.introduce IS '简介';
COMMENT ON COLUMN PY_USER.remark IS '描述';
COMMENT ON COLUMN PY_USER.role_code IS '角色编码';
COMMENT ON COLUMN PY_USER.status IS '用户状态';
COMMENT ON COLUMN PY_USER.create_by IS '创建人';
COMMENT ON COLUMN PY_USER.current_tenant_id IS '当前用户使用的租户id';
COMMENT ON COLUMN PY_USER.create_date_time IS '创建时间';
COMMENT ON COLUMN PY_USER.last_modified_by IS '更新人';
COMMENT ON COLUMN PY_USER.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN PY_USER.version_number IS '版本号';
COMMENT ON COLUMN PY_USER.deleted IS '逻辑删除';

-- 初始化系统管理员
INSERT INTO PY_USER (id, username, account, passwd, role_code, status, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number)
VALUES ('admin_id', '系统管理员', 'admin', '', 'ROLE_SYS_ADMIN', 'ENABLE', 'admin_id', NOW(), 'admin_id', NOW(), 0);

-- 租户用户关系表
CREATE TABLE IF NOT EXISTS PY_TENANT_USERS
(
  id                      VARCHAR(200)      NOT NULL UNIQUE PRIMARY KEY,
  user_id                 VARCHAR(200)      NOT NULL,
  tenant_id               VARCHAR(200)      NOT NULL,
  role_code               VARCHAR(200)      NOT NULL,
  status                  VARCHAR(200)      NOT NULL,
  remark                  VARCHAR(200),
  create_by               VARCHAR(200)      NOT NULL,
  create_date_time        TIMESTAMP         NOT NULL,
  last_modified_by        VARCHAR(200)      NOT NULL,
  last_modified_date_time TIMESTAMP         NOT NULL,
  version_number          INTEGER           NOT NULL,
  deleted                 INTEGER DEFAULT 0 NOT NULL
);

COMMENT ON TABLE PY_TENANT_USERS IS '租户用户关系表';
COMMENT ON COLUMN PY_TENANT_USERS.id IS '关系唯一id';
COMMENT ON COLUMN PY_TENANT_USERS.user_id IS '用户id';
COMMENT ON COLUMN PY_TENANT_USERS.tenant_id IS '租户id';
COMMENT ON COLUMN PY_TENANT_USERS.role_code IS '角色编码';
COMMENT ON COLUMN PY_TENANT_USERS.status IS '用户状态';
COMMENT ON COLUMN PY_TENANT_USERS.remark IS '备注';
COMMENT ON COLUMN PY_TENANT_USERS.create_by IS '创建人';
COMMENT ON COLUMN PY_TENANT_USERS.create_date_time IS '创建时间';
COMMENT ON COLUMN PY_TENANT_USERS.last_modified_by IS '更新人';
COMMENT ON COLUMN PY_TENANT_USERS.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN PY_TENANT_USERS.version_number IS '版本号';
COMMENT ON COLUMN PY_TENANT_USERS.deleted IS '逻辑删除';

-- 集群表
CREATE TABLE IF NOT EXISTS PY_CLUSTER
(
  id                      VARCHAR(200)      NOT NULL UNIQUE PRIMARY KEY,
  name                    VARCHAR(200)      NOT NULL,
  remark                  VARCHAR(500),
  status                  VARCHAR(200)      NOT NULL,
  check_date_time         TIMESTAMP         NOT NULL,
  all_node_num            INTEGER           NOT NULL,
  active_node_num         INTEGER           NOT NULL,
  all_memory_num          DOUBLE PRECISION  NOT NULL,
  used_memory_num         DOUBLE PRECISION  NOT NULL,
  all_storage_num         DOUBLE PRECISION  NOT NULL,
  used_storage_num        DOUBLE PRECISION  NOT NULL,
  create_by               VARCHAR(200)      NOT NULL,
  create_date_time        TIMESTAMP         NOT NULL,
  last_modified_by        VARCHAR(200)      NOT NULL,
  last_modified_date_time TIMESTAMP         NOT NULL,
  version_number          INTEGER           NOT NULL,
  deleted                 INTEGER DEFAULT 0 NOT NULL,
  tenant_id               VARCHAR(200)      NOT NULL
);

COMMENT ON TABLE PY_CLUSTER IS '集群表';
COMMENT ON COLUMN PY_CLUSTER.id IS '集群唯一id';
COMMENT ON COLUMN PY_CLUSTER.name IS '集群名称';
COMMENT ON COLUMN PY_CLUSTER.remark IS '集群描述';
COMMENT ON COLUMN PY_CLUSTER.status IS '集群状态';
COMMENT ON COLUMN PY_CLUSTER.check_date_time IS '检测时间';
COMMENT ON COLUMN PY_CLUSTER.all_node_num IS '所有节点';
COMMENT ON COLUMN PY_CLUSTER.active_node_num IS '激活节点数';
COMMENT ON COLUMN PY_CLUSTER.all_memory_num IS '所有内存';
COMMENT ON COLUMN PY_CLUSTER.used_memory_num IS '已使用内存';
COMMENT ON COLUMN PY_CLUSTER.all_storage_num IS '所有存储';
COMMENT ON COLUMN PY_CLUSTER.used_storage_num IS '已使用存储';
COMMENT ON COLUMN PY_CLUSTER.create_by IS '创建人';
COMMENT ON COLUMN PY_CLUSTER.create_date_time IS '创建时间';
COMMENT ON COLUMN PY_CLUSTER.last_modified_by IS '更新人';
COMMENT ON COLUMN PY_CLUSTER.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN PY_CLUSTER.version_number IS '版本号';
COMMENT ON COLUMN PY_CLUSTER.deleted IS '逻辑删除';
COMMENT ON COLUMN PY_CLUSTER.tenant_id IS '租户id';

-- 集群节点表
CREATE TABLE IF NOT EXISTS PY_CLUSTER_NODE
(
  id                      VARCHAR(200)                NOT NULL UNIQUE PRIMARY KEY,
  name                    VARCHAR(200)                NOT NULL,
  remark                  VARCHAR(500),
  status                  VARCHAR(200)                NOT NULL,
  check_date_time         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  all_memory              DOUBLE PRECISION            NOT NULL,
  used_memory             DOUBLE PRECISION            NOT NULL,
  all_storage             DOUBLE PRECISION            NOT NULL,
  used_storage            DOUBLE PRECISION            NOT NULL,
  cpu_percent             DOUBLE PRECISION            NOT NULL,
  cluster_id              VARCHAR(200)                NOT NULL,
  host                    VARCHAR(200)                NOT NULL,
  port                    VARCHAR(200)                NOT NULL,
  agent_log               VARCHAR(2000),
  username                VARCHAR(200)                NOT NULL,
  passwd                  VARCHAR(200)                NOT NULL,
  agent_home_path         VARCHAR(200)                NOT NULL,
  agent_port              VARCHAR(200)                NOT NULL,
  hadoop_home_path        VARCHAR(200),
  create_by               VARCHAR(200)                NOT NULL,
  create_date_time        TIMESTAMP                   NOT NULL,
  last_modified_by        VARCHAR(200)                NOT NULL,
  last_modified_date_time TIMESTAMP                   NOT NULL,
  deleted                 INTEGER DEFAULT 0           NOT NULL,
  tenant_id               VARCHAR(200)                NOT NULL
);

COMMENT ON TABLE PY_CLUSTER_NODE IS '集群节点表';
COMMENT ON COLUMN PY_CLUSTER_NODE.id IS '集群节点唯一id';
COMMENT ON COLUMN PY_CLUSTER_NODE.name IS '节点名称';
COMMENT ON COLUMN PY_CLUSTER_NODE.remark IS '节点描述';
COMMENT ON COLUMN PY_CLUSTER_NODE.status IS '节点状态';
COMMENT ON COLUMN PY_CLUSTER_NODE.check_date_time IS '检测时间';
COMMENT ON COLUMN PY_CLUSTER_NODE.all_memory IS '所有内存';
COMMENT ON COLUMN PY_CLUSTER_NODE.used_memory IS '已使用内存';
COMMENT ON COLUMN PY_CLUSTER_NODE.all_storage IS '所有存储';
COMMENT ON COLUMN PY_CLUSTER_NODE.used_storage IS '已使用存储';
COMMENT ON COLUMN PY_CLUSTER_NODE.cpu_percent IS 'cpu使用占比';
COMMENT ON COLUMN PY_CLUSTER_NODE.cluster_id IS '集群id';
COMMENT ON COLUMN PY_CLUSTER_NODE.host IS '节点服务器host';
COMMENT ON COLUMN PY_CLUSTER_NODE.port IS '节点服务器端口号';
COMMENT ON COLUMN PY_CLUSTER_NODE.agent_log IS '代理日志';
COMMENT ON COLUMN PY_CLUSTER_NODE.username IS '节点服务器用户名';
COMMENT ON COLUMN PY_CLUSTER_NODE.passwd IS '节点服务器';
COMMENT ON COLUMN PY_CLUSTER_NODE.agent_home_path IS '至慧云代理安装目录';
COMMENT ON COLUMN PY_CLUSTER_NODE.agent_port IS '至慧云代理服务端口号';
COMMENT ON COLUMN PY_CLUSTER_NODE.hadoop_home_path IS 'hadoop家目录';
COMMENT ON COLUMN PY_CLUSTER_NODE.create_by IS '创建人';
COMMENT ON COLUMN PY_CLUSTER_NODE.create_date_time IS '创建时间';
COMMENT ON COLUMN PY_CLUSTER_NODE.last_modified_by IS '更新人';
COMMENT ON COLUMN PY_CLUSTER_NODE.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN PY_CLUSTER_NODE.deleted IS '逻辑删除';
COMMENT ON COLUMN PY_CLUSTER_NODE.tenant_id IS '租户id';

-- 数据源表
CREATE TABLE IF NOT EXISTS PY_DATASOURCE
(
  id                      VARCHAR(200)      NOT NULL UNIQUE PRIMARY KEY,
  name                    VARCHAR(200)      NOT NULL,
  jdbc_url                VARCHAR(500)      NOT NULL,
  remark                  VARCHAR(500),
  status                  VARCHAR(200)      NOT NULL,
  check_date_time         TIMESTAMP         NOT NULL,
  username                VARCHAR(200),
  passwd                  VARCHAR(200),
  connect_log             VARCHAR(2000),
  db_type                 VARCHAR(200)      NOT NULL,
  create_by               VARCHAR(200)      NOT NULL,
  create_date_time        TIMESTAMP         NOT NULL,
  last_modified_by        VARCHAR(200)      NOT NULL,
  last_modified_date_time TIMESTAMP         NOT NULL,
  version_number          INTEGER           NOT NULL,
  deleted                 INTEGER DEFAULT 0 NOT NULL,
  tenant_id               VARCHAR(200)      NOT NULL
);

COMMENT ON TABLE PY_DATASOURCE IS '数据源表';
COMMENT ON COLUMN PY_DATASOURCE.id IS '数据源唯一id';
COMMENT ON COLUMN PY_DATASOURCE.name IS '数据源名称';
COMMENT ON COLUMN PY_DATASOURCE.jdbc_url IS '数据源jdbcUrl';
COMMENT ON COLUMN PY_DATASOURCE.remark IS '描述';
COMMENT ON COLUMN PY_DATASOURCE.status IS '状态';
COMMENT ON COLUMN PY_DATASOURCE.check_date_time IS '检测时间';
COMMENT ON COLUMN PY_DATASOURCE.username IS '数据源用户名';
COMMENT ON COLUMN PY_DATASOURCE.passwd IS '数据源密码';
COMMENT ON COLUMN PY_DATASOURCE.connect_log IS '测试连接日志';
COMMENT ON COLUMN PY_DATASOURCE.db_type IS '数据源类型';
COMMENT ON COLUMN PY_DATASOURCE.create_by IS '创建人';
COMMENT ON COLUMN PY_DATASOURCE.create_date_time IS '创建时间';
COMMENT ON COLUMN PY_DATASOURCE.last_modified_by IS '更新人';
COMMENT ON COLUMN PY_DATASOURCE.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN PY_DATASOURCE.version_number IS '版本号';
COMMENT ON COLUMN PY_DATASOURCE.deleted IS '逻辑删除';
COMMENT ON COLUMN PY_DATASOURCE.tenant_id IS '租户id';

-- 作业流表
CREATE TABLE IF NOT EXISTS PY_WORKFLOW
(
  id                      varchar(200)  not null unique primary key,
  name                    varchar(200)  not null,
  remark                  varchar(500),
  status                  varchar(200)  not null,
  create_by               varchar(200)  not null,
  create_date_time        timestamp     not null,
  last_modified_by        varchar(200)  not null,
  last_modified_date_time timestamp     not null,
  version_number          int           not null,
  deleted                 int default 0 not null,
  tenant_id               varchar(200)  not null
);

COMMENT ON COLUMN PY_WORKFLOW.id IS '作业流唯一id';
COMMENT ON COLUMN PY_WORKFLOW.name IS '作业流名称';
COMMENT ON COLUMN PY_WORKFLOW.remark IS '作业流描述';
COMMENT ON COLUMN PY_WORKFLOW.status IS '状态';
COMMENT ON COLUMN PY_WORKFLOW.create_by IS '创建人';
COMMENT ON COLUMN PY_WORKFLOW.create_date_time IS '创建时间';
COMMENT ON COLUMN PY_WORKFLOW.last_modified_by IS '更新人';
COMMENT ON COLUMN PY_WORKFLOW.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN PY_WORKFLOW.version_number IS '版本号';
COMMENT ON COLUMN PY_WORKFLOW.deleted IS '逻辑删除';
COMMENT ON COLUMN PY_WORKFLOW.tenant_id IS '租户id';

-- 作业表
CREATE TABLE IF NOT EXISTS PY_WORK
(
  id                      varchar(200)  not null unique primary key,
  name                    varchar(200)  not null,
  remark                  varchar(500),
  status                  varchar(200)  not null,
  work_type               varchar(200)  not null,
  config_id               varchar(200)  not null,
  workflow_id             varchar(200)  not null,
  version_id              varchar(200),
  create_by               varchar(200)  not null,
  create_date_time        timestamp     not null,
  last_modified_by        varchar(200)  not null,
  last_modified_date_time timestamp     not null,
  version_number          int           not null,
  deleted                 int default 0 not null,
  tenant_id               varchar(200)  not null
);

COMMENT ON COLUMN PY_WORK.id IS '作业唯一id';
COMMENT ON COLUMN PY_WORK.name IS '作业名称';
COMMENT ON COLUMN PY_WORK.remark IS '作业描述';
COMMENT ON COLUMN PY_WORK.status IS '作业状态';
COMMENT ON COLUMN PY_WORK.work_type IS '作业类型';
COMMENT ON COLUMN PY_WORK.config_id IS '作业配置id';
COMMENT ON COLUMN PY_WORK.workflow_id IS '作业流id';
COMMENT ON COLUMN PY_WORK.version_id IS '作业当前最新版本号';
COMMENT ON COLUMN PY_WORK.create_by IS '创建人';
COMMENT ON COLUMN PY_WORK.create_date_time IS '创建时间';
COMMENT ON COLUMN PY_WORK.last_modified_by IS '更新人';
COMMENT ON COLUMN PY_WORK.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN PY_WORK.version_number IS '版本号';
COMMENT ON COLUMN PY_WORK.deleted IS '逻辑删除';
COMMENT ON COLUMN PY_WORK.tenant_id IS '租户id';

-- 作业配置表
CREATE TABLE IF NOT EXISTS PY_WORK_CONFIG
(
  id                      varchar(200)  not null unique primary key,
  datasource_id           varchar(200),
  cluster_id              varchar(200),
  spark_config            text,
  sql_script              text,
  corn                    varchar(200),
  create_by               varchar(200)  not null,
  create_date_time        timestamp     not null,
  last_modified_by        varchar(200)  not null,
  last_modified_date_time timestamp     not null,
  version_number          int           not null,
  deleted                 int default 0 not null,
  tenant_id               varchar(200)  not null
);

COMMENT ON COLUMN PY_WORK_CONFIG.id IS '作业配置唯一id';
COMMENT ON COLUMN PY_WORK_CONFIG.datasource_id IS '数据源id';
COMMENT ON COLUMN PY_WORK_CONFIG.cluster_id IS '集群id';
COMMENT ON COLUMN PY_WORK_CONFIG.spark_config IS 'spark的作业配置';
COMMENT ON COLUMN PY_WORK_CONFIG.sql_script IS 'sql脚本';
COMMENT ON COLUMN PY_WORK_CONFIG.corn IS '定时表达式';
COMMENT ON COLUMN PY_WORK_CONFIG.create_by IS '创建人';
COMMENT ON COLUMN PY_WORK_CONFIG.create_date_time IS '创建时间';
COMMENT ON COLUMN PY_WORK_CONFIG.last_modified_by IS '更新人';
COMMENT ON COLUMN PY_WORK_CONFIG.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN PY_WORK_CONFIG.version_number IS '版本号';
COMMENT ON COLUMN PY_WORK_CONFIG.deleted IS '逻辑删除';
COMMENT ON COLUMN PY_WORK_CONFIG.tenant_id IS '租户id';

-- 许可证表
CREATE TABLE IF NOT EXISTS PY_LICENSE
(
  id                      varchar(200)  not null unique primary key,
  code                    varchar(200)  not null,
  company_name            varchar(200)  not null,
  logo                    varchar(2000) not null,
  remark                  varchar(2000),
  issuer                  varchar(200)  not null,
  start_date_time         timestamp     not null,
  end_date_time           timestamp     not null,
  max_tenant_num          int,
  max_member_num          int,
  max_workflow_num        int,
  status                  varchar(200)  not null,
  create_by               varchar(200)  not null,
  create_date_time        timestamp     not null,
  last_modified_by        varchar(200)  not null,
  last_modified_date_time timestamp     not null,
  version_number          int           not null,
  deleted                 int default 0 not null
);

COMMENT ON COLUMN PY_LICENSE.id IS '许可证唯一id';
COMMENT ON COLUMN PY_LICENSE.code IS '许可证编号';
COMMENT ON COLUMN PY_LICENSE.company_name IS '公司名称';
COMMENT ON COLUMN PY_LICENSE.logo IS '公司logo';
COMMENT ON COLUMN PY_LICENSE.remark IS '许可证备注';
COMMENT ON COLUMN PY_LICENSE.issuer IS '许可证签发人';
COMMENT ON COLUMN PY_LICENSE.start_date_time IS '许可证起始时间';
COMMENT ON COLUMN PY_LICENSE.end_date_time IS '许可证到期时间';
COMMENT ON COLUMN PY_LICENSE.max_tenant_num IS '最大租户数';
COMMENT ON COLUMN PY_LICENSE.max_member_num IS '最大成员数';
COMMENT ON COLUMN PY_LICENSE.max_workflow_num IS '最大作业流数';
COMMENT ON COLUMN PY_LICENSE.status IS '证书状态';
COMMENT ON COLUMN PY_LICENSE.create_by IS '创建人';
COMMENT ON COLUMN PY_LICENSE.create_date_time IS '创建时间';
COMMENT ON COLUMN PY_LICENSE.last_modified_by IS '更新人';
COMMENT ON COLUMN PY_LICENSE.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN PY_LICENSE.version_number IS '版本号';
COMMENT ON COLUMN PY_LICENSE.deleted IS '逻辑删除';

-- 自定义API表
CREATE TABLE IF NOT EXISTS PY_API
(
  id                      varchar(200)  not null unique primary key,
  name                    varchar(200)  not null,
  path                    varchar(200)  not null,
  api_type                varchar(200)  not null,
  remark                  varchar(2000),
  req_header              varchar(2000),
  req_body                varchar(2000),
  api_sql                 varchar(2000) not null,
  res_body                varchar(2000) not null,
  datasource_id           varchar(200)  not null,
  status                  varchar(200)  not null,
  create_by               varchar(200)  not null,
  create_date_time        timestamp     not null,
  last_modified_by        varchar(200)  not null,
  last_modified_date_time timestamp     not null,
  version_number          int           not null,
  deleted                 int default 0 not null,
  tenant_id               varchar(200)  not null
);

COMMENT ON COLUMN PY_API.id IS '唯一API的id';
COMMENT ON COLUMN PY_API.name IS 'API名称';
COMMENT ON COLUMN PY_API.path IS 'API访问地址';
COMMENT ON COLUMN PY_API.api_type IS 'API类型';
COMMENT ON COLUMN PY_API.remark IS 'API备注';
COMMENT ON COLUMN PY_API.req_header IS '请求头';
COMMENT ON COLUMN PY_API.req_body IS '请求体';
COMMENT ON COLUMN PY_API.api_sql IS '执行的sql';
COMMENT ON COLUMN PY_API.res_body IS '响应体';
COMMENT ON COLUMN PY_API.datasource_id IS '数据源id';
COMMENT ON COLUMN PY_API.status IS 'API状态';
COMMENT ON COLUMN PY_API.create_by IS '创建人';
COMMENT ON COLUMN PY_API.create_date_time IS '创建时间';
COMMENT ON COLUMN PY_API.last_modified_by IS '更新人';
COMMENT ON COLUMN PY_API.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN PY_API.version_number IS '版本号';
COMMENT ON COLUMN PY_API.deleted IS '逻辑删除';
COMMENT ON COLUMN PY_API.tenant_id IS '租户id';

-- 作业配置版本表
CREATE TABLE IF NOT EXISTS PY_WORK_VERSION
(
  id                      varchar(200)  not null unique primary key,
  work_id                 varchar(200)  not null,
  work_type               varchar(200)  not null,
  datasource_id           varchar(200),
  cluster_id              varchar(200),
  sql_script              text,
  spark_config            text,
  corn                    varchar(200)  not null,
  create_by               varchar(200)  not null,
  create_date_time        timestamp     not null,
  last_modified_by        varchar(200)  not null,
  last_modified_date_time timestamp     not null,
  version_number          int           not null,
  deleted                 int default 0 not null,
  tenant_id               varchar(200)  not null
);

COMMENT ON COLUMN PY_WORK_VERSION.id IS '版本唯一id';
COMMENT ON COLUMN PY_WORK_VERSION.work_id IS '作业id';
COMMENT ON COLUMN PY_WORK_VERSION.work_type IS '作业类型';
COMMENT ON COLUMN PY_WORK_VERSION.datasource_id IS '数据源id';
COMMENT ON COLUMN PY_WORK_VERSION.cluster_id IS '集群id';
COMMENT ON COLUMN PY_WORK_VERSION.sql_script IS 'sql脚本';
COMMENT ON COLUMN PY_WORK_VERSION.spark_config IS 'spark的作业配置';
COMMENT ON COLUMN PY_WORK_VERSION.corn IS '定时表达式';
COMMENT ON COLUMN PY_WORK_VERSION.create_by IS '创建人';
COMMENT ON COLUMN PY_WORK_VERSION.create_date_time IS '创建时间';
COMMENT ON COLUMN PY_WORK_VERSION.last_modified_by IS '更新人';
COMMENT ON COLUMN PY_WORK_VERSION.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN PY_WORK_VERSION.version_number IS '版本号';
COMMENT ON COLUMN PY_WORK_VERSION.deleted IS '逻辑删除';
COMMENT ON COLUMN PY_WORK_VERSION.tenant_id IS '租户id';

-- 作业运行实例表
CREATE TABLE IF NOT EXISTS PY_WORK_INSTANCE
(
  id                      varchar(200)  not null unique primary key,
  version_id              varchar(200),
  work_id                 varchar(200),
  instance_type           varchar(200),
  status                  varchar(200),
  plan_start_date_time    timestamp,
  next_plan_date_time     timestamp,
  exec_start_date_time    timestamp,
  exec_end_date_time      timestamp,
  submit_log              text,
  yarn_log                text,
  spark_star_res          varchar(2000),
  result_data             text,
  create_by               varchar(200)  not null,
  create_date_time        timestamp     not null,
  last_modified_by        varchar(200)  not null,
  last_modified_date_time timestamp     not null,
  deleted                 int default 0 not null,
  tenant_id               varchar(200)  not null
);

COMMENT ON COLUMN PY_WORK_INSTANCE.id IS '实例唯一id';
COMMENT ON COLUMN PY_WORK_INSTANCE.version_id IS '实例版本id';
COMMENT ON COLUMN PY_WORK_INSTANCE.work_id IS '作业id';
COMMENT ON COLUMN PY_WORK_INSTANCE.instance_type IS '实例类型';
COMMENT ON COLUMN PY_WORK_INSTANCE.status IS '实例状态';
COMMENT ON COLUMN PY_WORK_INSTANCE.plan_start_date_time IS '计划开始时间';
COMMENT ON COLUMN PY_WORK_INSTANCE.next_plan_date_time IS '下一次开始时间';
COMMENT ON COLUMN PY_WORK_INSTANCE.exec_start_date_time IS '执行开始时间';
COMMENT ON COLUMN PY_WORK_INSTANCE.exec_end_date_time IS '执行结束时间';
COMMENT ON COLUMN PY_WORK_INSTANCE.submit_log IS '提交日志';
COMMENT ON COLUMN PY_WORK_INSTANCE.yarn_log IS 'yarn日志';
COMMENT ON COLUMN PY_WORK_INSTANCE.spark_star_res IS 'spark-star插件返回';
COMMENT ON COLUMN PY_WORK_INSTANCE.result_data IS '结果数据';
COMMENT ON COLUMN PY_WORK_INSTANCE.create_by IS '创建人';
COMMENT ON COLUMN PY_WORK_INSTANCE.create_date_time IS '创建时间';
COMMENT ON COLUMN PY_WORK_INSTANCE.last_modified_by IS '更新人';
COMMENT ON COLUMN PY_WORK_INSTANCE.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN PY_WORK_INSTANCE.deleted IS '逻辑删除';
COMMENT ON COLUMN PY_WORK_INSTANCE.tenant_id IS '租户id';

-- 用户行为记录表
CREATE TABLE IF NOT EXISTS PY_USER_ACTION
(
  id               varchar(200) not null unique primary key,
  user_id          varchar(200),
  tenant_id        varchar(200),
  req_path         varchar(200),
  req_method       varchar(200),
  req_header       varchar(2000),
  req_body         text,
  res_body         text,
  start_timestamp  bigint,
  end_timestamp    bigint,
  create_by        varchar(200) not null,
  create_date_time timestamp    not null
);

COMMENT ON COLUMN PY_USER_ACTION.id IS '用户行为唯一id';
COMMENT ON COLUMN PY_USER_ACTION.user_id IS '用户id';
COMMENT ON COLUMN PY_USER_ACTION.tenant_id IS '租户id';
COMMENT ON COLUMN PY_USER_ACTION.req_path IS '请求路径';
COMMENT ON COLUMN PY_USER_ACTION.req_method IS '请求方式';
COMMENT ON COLUMN PY_USER_ACTION.req_header IS '请求头';
COMMENT ON COLUMN PY_USER_ACTION.req_body IS '请求体';
COMMENT ON COLUMN PY_USER_ACTION.res_body IS '响应体';
COMMENT ON COLUMN PY_USER_ACTION.start_timestamp IS '开始时间戳';
COMMENT ON COLUMN PY_USER_ACTION.end_timestamp IS '结束时间戳';
COMMENT ON COLUMN PY_USER_ACTION.create_by IS '创建人';
COMMENT ON COLUMN PY_USER_ACTION.create_date_time IS '创建时间';

-- 0.0.6
ALTER TABLE PY_CLUSTER
  ADD COLUMN cluster_type varchar(100) null;
COMMENT ON COLUMN PY_CLUSTER.cluster_type IS '集群的类型';

UPDATE PY_CLUSTER
SET cluster_type='yarn'
WHERE 1 = 1;

-- 0.1.0
-- 给作业实例，添加作业流实例id
ALTER TABLE PY_WORK_INSTANCE
  ADD COLUMN workflow_instance_id varchar(100) null;
COMMENT ON COLUMN PY_WORK_INSTANCE.workflow_instance_id IS '工作流实例id';

-- 给作业实例，添加是否被定时器触发过
ALTER TABLE PY_WORK_INSTANCE
  ADD COLUMN quartz_has_run boolean null;
COMMENT ON COLUMN PY_WORK_INSTANCE.quartz_has_run IS '是否被定时器触发过';

-- 创建工作流配置表
CREATE TABLE PY_WORKFLOW_CONFIG
(
  id                      varchar(200)  not null,
  web_config              text          null,
  node_mapping            varchar(2000) null,
  node_list               varchar(2000) null,
  dag_start_list          varchar(2000) null,
  dag_end_list            varchar(2000) null,
  create_by               varchar(200)  not null,
  create_date_time        timestamp     not null,
  last_modified_by        varchar(200)  not null,
  last_modified_date_time timestamp     not null,
  version_number          int           not null,
  deleted                 int default 0 not null,
  tenant_id               varchar(200)  not null,
  corn                    varchar(300)  null,
  CONSTRAINT PY_WORKFLOW_CONFIG_pk PRIMARY KEY (id)
);

-- 创建工作流实例表
CREATE TABLE PY_WORKFLOW_INSTANCE
(
  id                      varchar(200)  not null,
  version_id              varchar(200)  null,
  flow_id                 varchar(200)  null,
  instance_type           varchar(200)  null,
  status                  varchar(200)  null,
  run_log                 text          null,
  web_config              text          null,
  plan_start_date_time    timestamp     null,
  next_plan_date_time     timestamp     null,
  exec_start_date_time    timestamp     null,
  exec_end_date_time      timestamp     null,
  create_by               varchar(200)  not null,
  create_date_time        timestamp     not null,
  last_modified_by        varchar(200)  not null,
  last_modified_date_time timestamp     not null,
  deleted                 int default 0 not null,
  tenant_id               varchar(200)  not null,
  CONSTRAINT PY_WORKFLOW_INSTANCE_pk PRIMARY KEY (id)
);

-- 创建工作流版本表
CREATE TABLE PY_WORKFLOW_VERSION
(
  id                      varchar(200)  not null,
  name                    varchar(200)  not null,
  workflow_id             varchar(200)  not null,
  workflow_type           varchar(200)  null,
  node_mapping            text          null,
  node_list               text          null,
  dag_start_list          text          null,
  dag_end_list            text          null,
  work_version_map        text          null,
  corn                    varchar(200)  not null,
  create_by               varchar(200)  not null,
  create_date_time        timestamp     not null,
  last_modified_by        varchar(200)  not null,
  last_modified_date_time timestamp     not null,
  version_number          int           not null,
  deleted                 int default 0 not null,
  tenant_id               varchar(200)  not null,
  CONSTRAINT PY_WORKFLOW_VERSION_pk PRIMARY KEY (id)
);

-- 工作流添加工作流配置id
ALTER TABLE PY_WORKFLOW
  ADD COLUMN config_id varchar(100) null;
COMMENT ON COLUMN PY_WORKFLOW.config_id IS '工作流配置id';

-- 工作流添加工作流当前版本id
ALTER TABLE PY_WORKFLOW
  ADD COLUMN version_id varchar(100) null;
COMMENT ON COLUMN PY_WORKFLOW.version_id IS '版本id';

-- 工作流添加工作流类型
ALTER TABLE PY_WORKFLOW
  ADD COLUMN type varchar(200) null;
COMMENT ON COLUMN PY_WORKFLOW.type IS '工作流类型';

-- 作业添加置顶标志
ALTER TABLE PY_WORK
  ADD COLUMN top_index int null;
COMMENT ON COLUMN PY_WORK.top_index IS '作业置顶标志';

-- 工作流收藏表
CREATE TABLE PY_WORKFLOW_FAVOUR
(
  id                      varchar(200)  not null,
  workflow_id             varchar(200)  null,
  user_id                 varchar(200)  null,
  top_index               int           null,
  create_by               varchar(200)  not null,
  create_date_time        timestamp     not null,
  last_modified_by        varchar(200)  not null,
  last_modified_date_time timestamp     not null,
  version_number          int           not null,
  deleted                 int default 0 not null,
  tenant_id               varchar(200)  not null,
  CONSTRAINT PY_WORKFLOW_FAVOUR_pk PRIMARY KEY (id)
);

-- 自定义锁表
CREATE TABLE PY_LOCKER
(
  id   SERIAL PRIMARY KEY,
  name VARCHAR(2000) NOT NULL,
  box  VARCHAR(2000)
);

ALTER TABLE PY_CLUSTER_NODE
  ALTER COLUMN passwd TYPE VARCHAR(5000) USING passwd::VARCHAR(5000),
  ALTER COLUMN passwd SET NOT NULL;
COMMENT ON COLUMN PY_CLUSTER_NODE.passwd IS '节点服务器密码';

--0.2.0
-- 创建资源文件表
CREATE TABLE PY_FILE
(
  id                      varchar(200)  not null,
  file_name               varchar(200)  null,
  file_size               varchar(200)  null,
  file_path               varchar(200)  null,
  file_type               varchar(200)  null,
  create_by               varchar(200)  not null,
  create_date_time        timestamp     not null,
  last_modified_by        varchar(200)  not null,
  last_modified_date_time timestamp     not null,
  version_number          int           not null,
  deleted                 int default 0 not null,
  tenant_id               varchar(200)  not null,
  CONSTRAINT PY_FILE_pk PRIMARY KEY (id)
);

-- 添加同步作业的配置
ALTER TABLE PY_WORK_CONFIG
  ADD COLUMN sync_work_config text null;

-- 统一脚本内容
ALTER TABLE PY_WORK_CONFIG
  RENAME COLUMN sql_script TO script;
COMMENT ON COLUMN PY_WORK_CONFIG.script IS '统一脚本内容，包括sql、bash、python脚本';

-- 将cluster_id和spark_config合并成cluster_config
ALTER TABLE PY_WORK_CONFIG
  DROP COLUMN cluster_id;
ALTER TABLE PY_WORK_CONFIG
  DROP COLUMN spark_config;

ALTER TABLE PY_WORK_CONFIG
  ADD COLUMN cluster_config text null;
COMMENT ON COLUMN PY_WORK_CONFIG.cluster_config IS '计算集群配置';

-- 将cron扩展成cron_config
ALTER TABLE PY_WORK_CONFIG
  RENAME COLUMN corn TO cron_config;

-- 添加数据同步规则
ALTER TABLE PY_WORK_CONFIG
  ADD COLUMN sync_rule text null;
COMMENT ON COLUMN PY_WORK_CONFIG.sync_rule IS '数据同步规则';

-- 添加spark_home_path
ALTER TABLE PY_CLUSTER_NODE
  ADD COLUMN spark_home_path varchar(200) null;
COMMENT ON COLUMN PY_CLUSTER_NODE.spark_home_path IS 'standalone模式spark的安装目录';

-- 添加默认集群
ALTER TABLE PY_CLUSTER
  ADD COLUMN default_cluster boolean null default false;
COMMENT ON COLUMN PY_CLUSTER.default_cluster IS '默认计算集群';

-- 作业运行实例中，添加作业运行的pid
ALTER TABLE PY_WORK_INSTANCE
  ADD COLUMN work_pid varchar(100) null;
COMMENT ON COLUMN PY_WORK_INSTANCE.work_pid IS '作业运行的进程pid';

-- hive数据源，添加hive.metastore.uris配置项
ALTER TABLE PY_DATASOURCE
  ADD COLUMN metastore_uris varchar(200) null;
COMMENT ON COLUMN PY_DATASOURCE.metastore_uris IS 'hive数据源 hive.metastore.uris 配置';

-- 数据源支持驱动添加
ALTER TABLE PY_DATASOURCE
  ADD COLUMN driver_id varchar(100),
  ALTER COLUMN driver_id SET NOT NULL;
COMMENT ON COLUMN PY_DATASOURCE.driver_id IS '数据库驱动id';

-- 新增数据源驱动表
CREATE TABLE PY_DATABASE_DRIVER
(
  id                      varchar(200)  not null,
  name                    varchar(200)  not null,
  db_type                 varchar(200)  not null,
  file_name               varchar(500)  not null,
  remark                  varchar(500)  null,
  driver_type             varchar(200)  not null,
  is_default_driver       boolean       not null,
  create_by               varchar(200)  not null,
  create_date_time        timestamp     not null,
  last_modified_by        varchar(200)  not null,
  last_modified_date_time timestamp     not null,
  version_number          int           not null,
  deleted                 int default 0 not null,
  tenant_id               varchar(200)  not null,
  CONSTRAINT PY_DATABASE_DRIVER_pk PRIMARY KEY (id)
);

-- 初始化系统驱动
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver)
VALUES ('clickhouse_0.5.0', 'clickhouse_0.5.0', 'CLICKHOUSE', 'clickhouse-jdbc-0.5.0.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', true);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver)
VALUES ('db2_11.5.8.0', 'db2_11.5.8.0', 'DB2', 'jcc-11.5.8.0.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', true);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver)
VALUES ('dm_8.1.1.49', 'dm_8.1.1.49', 'DM', 'Dm8JdbcDriver18-8.1.1.49.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', true);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver)
VALUES ('doris_mysql_5.1.49', 'doris_mysql_5.1.49', 'DORIS', 'mysql-connector-java-5.1.49.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', true);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver)
VALUES ('hana_2.18.13', 'hana_2.18.13', 'HANA_SAP', 'ngdbc-2.18.13.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', true);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver)
VALUES ('hive_uber_2.6.3.jar', 'hive_uber_2.6.3.jar', 'HIVE', 'hive-jdbc-uber-2.6.3.0-235.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', true);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver)
VALUES ('hive_3.1.3', 'hive_3.1.3', 'HIVE', 'hive-jdbc-3.1.3-standalone.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', false);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver)
VALUES ('mysql_5.1.49', 'mysql_5.1.49', 'MYSQL', 'mysql-connector-java-5.1.49.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', false);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver)
VALUES ('mysql_8.1.0', 'mysql_8.1.0', 'MYSQL', 'mysql-connector-j-8.1.0.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', true);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver)
VALUES ('oceanbase_2.4.6', 'oceanbase_2.4.6', 'OCEANBASE', 'oceanbase-client-2.4.6.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', true);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver)
VALUES ('oracle_19.20.0.0', 'oracle_19.20.0.0', 'ORACLE', 'ojdbc10-19.20.0.0.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', true);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver)
VALUES ('postgre_42.6.0', 'postgre_42.6.0', 'POSTGRE_SQL', 'postgresql-42.6.0.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', true);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver)
VALUES ('sqlServer_12.4.2.jre8', 'sqlServer_12.4.2.jre8', 'SQL_SERVER', 'mssql-jdbc-12.4.2.jre8.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', true);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver)
VALUES ('star_rocks(mysql_5.1.49)', 'star_rocks(mysql_5.1.49)', 'STAR_ROCKS', 'mysql-connector-java-5.1.49.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', true);
INSERT INTO PY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver)
VALUES ('tidb(mysql_5.1.49)', 'tidb(mysql_5.1.49)', 'TIDB', 'mysql-connector-java-5.1.49.jar', 'SYSTEM_DRIVER', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun', '系统自带驱动', true);

-- 工作流添加默认计算引擎配置
ALTER TABLE PY_WORKFLOW
  ADD COLUMN default_cluster_id VARCHAR(200) NULL;
COMMENT ON COLUMN PY_WORKFLOW.default_cluster_id IS '默认计算引擎';

-- 数据源日志长度扩大
ALTER TABLE PY_DATASOURCE
  ALTER COLUMN connect_log TYPE TEXT using connect_log::text;
COMMENT ON COLUMN PY_DATASOURCE.connect_log IS '测试连接日志';

-- PY_WORKFLOW_CONFIG将cron扩展成cron_config
ALTER TABLE PY_WORKFLOW_CONFIG
  RENAME COLUMN corn TO cron_config;
ALTER TABLE PY_WORKFLOW_CONFIG
  ALTER COLUMN cron_config TYPE TEXT using cron_config::text;
COMMENT ON COLUMN PY_WORKFLOW_CONFIG.cron_config IS '定时表达式';

-- PY_WORK_VERSION将cron扩展成cron_config
ALTER TABLE PY_WORK_VERSION
  RENAME COLUMN corn TO cron_config;
ALTER TABLE PY_WORK_VERSION
  ALTER COLUMN cron_config TYPE TEXT using cron_config::text;
COMMENT ON COLUMN PY_WORK_VERSION.cron_config IS '定时表达配置';

-- PY_WORK_VERSION将cluster_id扩展成cluster_config
ALTER TABLE PY_WORK_VERSION
  RENAME COLUMN cluster_id TO cluster_config;
ALTER TABLE PY_WORK_VERSION
  ALTER COLUMN cluster_config TYPE TEXT;
COMMENT ON COLUMN PY_WORK_VERSION.cluster_config IS '集群配置';

-- PY_WORK_VERSION将sql_script扩展成script
ALTER TABLE PY_WORK_VERSION
  RENAME COLUMN sql_script TO script;
ALTER TABLE PY_WORK_VERSION
  ALTER COLUMN script TYPE TEXT using script::text;
COMMENT ON COLUMN PY_WORK_VERSION.script IS '脚本内容';

-- 删除PY_WORK_VERSION的spark_config
alter table PY_WORK_VERSION
  drop column spark_config;

-- 添加同步作业的配置
ALTER TABLE PY_WORK_VERSION
  ADD COLUMN sync_work_config TEXT NULL;
COMMENT ON COLUMN PY_WORK_VERSION.sync_work_config IS '同步作业的配置';

-- 添加数据同步规则
ALTER TABLE PY_WORK_VERSION
  ADD COLUMN sync_rule TEXT NULL;
COMMENT ON COLUMN PY_WORK_VERSION.sync_rule IS '数据同步规则';

-- 修改错别字
ALTER TABLE PY_WORKFLOW_VERSION
  RENAME COLUMN corn TO cron_config;
ALTER TABLE PY_WORKFLOW_VERSION
  ALTER COLUMN cron_config TYPE VARCHAR(200) using cron_config::VARCHAR(200);
COMMENT ON COLUMN PY_WORKFLOW_VERSION.cron_config IS '定时表达式';

-- 作业流版本里加dag图
ALTER TABLE PY_WORKFLOW_VERSION
  ADD COLUMN web_config TEXT NULL;
COMMENT ON COLUMN PY_WORKFLOW_VERSION.web_config IS '作业流的dag图';

-- 作业流实例添加耗时
ALTER TABLE PY_WORKFLOW_INSTANCE
  ADD COLUMN duration INT NULL;
COMMENT ON COLUMN PY_WORKFLOW_INSTANCE.duration IS '耗时时间（秒）';

-- 作业流实例添加耗时
ALTER TABLE PY_WORK_INSTANCE
  ADD COLUMN duration INT NULL;
COMMENT ON COLUMN PY_WORK_INSTANCE.duration IS '耗时时间（秒）';

-- 222
-- 作业添加udf函数开关
ALTER TABLE PY_WORK_CONFIG
  ADD udf_status BOOL DEFAULT false;

ALTER TABLE PY_WORK_CONFIG
  ADD COLUMN jar_conf text NULL;
COMMENT ON COLUMN PY_WORK_CONFIG.jar_conf IS 'JAR 配置';

ALTER TABLE PY_WORKFLOW_CONFIG
  ADD COLUMN external_call CHAR NULL;
COMMENT ON COLUMN PY_WORKFLOW_CONFIG.external_call IS '外部调用标志';

ALTER TABLE PY_WORKFLOW_CONFIG
  ADD COLUMN access_key varchar(100) NULL;
COMMENT ON COLUMN PY_WORKFLOW_CONFIG.access_key IS '访问密钥';

ALTER TABLE PY_API
  ADD COLUMN token_type varchar(200) NULL;
COMMENT ON COLUMN PY_API.token_type IS '认证方式';

ALTER TABLE PY_API
  ADD COLUMN page_type varchar(50) NULL;
COMMENT ON COLUMN PY_API.page_type IS '分页状态';

-- 489
-- 删除资源文件的路径
ALTER TABLE PY_FILE
  DROP COLUMN FILE_PATH;

-- 添加备注字段
ALTER TABLE PY_FILE
  ADD COLUMN REMARK varchar(500);
COMMENT ON COLUMN PY_FILE.REMARK IS '备注';

-- 添加自定义jar作业配置
ALTER TABLE PY_WORK_CONFIG
  ADD COLUMN JAR_JOB_CONFIG text;
COMMENT ON COLUMN PY_WORK_CONFIG.JAR_JOB_CONFIG IS '自定义jar作业配置';

-- 版本添加自定义作业配置
ALTER TABLE PY_WORK_VERSION
  ADD COLUMN JAR_JOB_CONFIG text;
COMMENT ON COLUMN PY_WORK_VERSION.JAR_JOB_CONFIG IS '自定义作业配置';

-- 依赖配置
ALTER TABLE PY_WORK_CONFIG
  ADD COLUMN LIB_CONFIG text;
COMMENT ON COLUMN PY_WORK_CONFIG.LIB_CONFIG IS '作业依赖文件';

-- 自定义配置
ALTER TABLE PY_WORK_CONFIG
  ADD COLUMN FUNC_CONFIG text;
COMMENT ON COLUMN PY_WORK_CONFIG.FUNC_CONFIG IS '自定义函数配置';

ALTER TABLE PY_WORK_VERSION
  ADD COLUMN LIB_CONFIG text;
COMMENT ON COLUMN PY_WORK_VERSION.LIB_CONFIG IS '依赖配置';

ALTER TABLE PY_WORK_VERSION
  ADD COLUMN FUNC_CONFIG text;
COMMENT ON COLUMN PY_WORK_VERSION.FUNC_CONFIG IS '自定义函数配置';

-- 作业支持容器sql
ALTER TABLE PY_WORK_CONFIG
  ADD COLUMN CONTAINER_ID varchar(200);

-- 作业版本支持容器sql
ALTER TABLE PY_WORK_VERSION
  ADD COLUMN CONTAINER_ID varchar(200);

-- 348
-- 添加接口调用作业配置
ALTER TABLE PY_WORK_CONFIG
  ADD COLUMN API_WORK_CONFIG text NULL;
COMMENT ON COLUMN PY_WORK_CONFIG.API_WORK_CONFIG IS '接口调用作业的配置';

-- 版本中添加接口调用作业配置
ALTER TABLE PY_WORK_VERSION
  ADD COLUMN API_WORK_CONFIG text NULL;
COMMENT ON COLUMN PY_WORK_VERSION.API_WORK_CONFIG IS '接口调用作业的配置';

-- 新增kafka数据源配置
ALTER TABLE PY_DATASOURCE
  ADD COLUMN KAFKA_CONFIG text NULL;
COMMENT ON COLUMN PY_DATASOURCE.KAFKA_CONFIG IS 'kafka数据源配置';

-- 新增kafka驱动
INSERT INTO PY_DATABASE_DRIVER (ID, NAME, DB_TYPE, FILE_NAME, DRIVER_TYPE, IS_DEFAULT_DRIVER, REMARK, CREATE_BY, CREATE_DATE_TIME, LAST_MODIFIED_BY, LAST_MODIFIED_DATE_TIME, VERSION_NUMBER, DELETED, TENANT_ID)
VALUES ('kafka_client_3.1.2', 'kafka_client_3.1.2', 'KAFKA', 'kafka_client_3.1.2.jar', 'SYSTEM_DRIVER', true, '系统自带驱动', 'zhihuiyun', '2023-11-01 16:54:34', 'zhihuiyun', '2023-11-01 16:54:39', 1, 0, 'zhihuiyun');

-- 503
-- 添加监控表
CREATE TABLE PY_MONITOR
(
  id                     varchar(200)     NOT NULL PRIMARY KEY,
  cluster_id             varchar(200)     NOT NULL,
  cluster_node_id        varchar(200)     NOT NULL,
  status                 varchar(100)     NOT NULL,
  log                    text             NOT NULL,
  used_storage_size      bigint           NULL,
  used_memory_size       bigint           NULL,
  network_io_read_speed  bigint           NULL,
  network_io_write_speed bigint           NULL,
  disk_io_read_speed     bigint           NULL,
  disk_io_write_speed    bigint           NULL,
  cpu_percent            double precision NULL, -- 注意：在PostgreSQL中，通常使用double precision
  create_date_time       timestamp        NOT NULL,
  deleted                int DEFAULT 0    NOT NULL,
  tenant_id              varchar(200)     NOT NULL
);

-- 为列添加注释
COMMENT ON COLUMN PY_MONITOR.id IS '分享表单链接id';
COMMENT ON COLUMN PY_MONITOR.cluster_id IS '集群id';
COMMENT ON COLUMN PY_MONITOR.cluster_node_id IS '集群节点id';
COMMENT ON COLUMN PY_MONITOR.status IS '监控状态';
COMMENT ON COLUMN PY_MONITOR.log IS '日志';
COMMENT ON COLUMN PY_MONITOR.used_storage_size IS '已使用存储';
COMMENT ON COLUMN PY_MONITOR.used_memory_size IS '已使用内存';
COMMENT ON COLUMN PY_MONITOR.network_io_read_speed IS '网络读速度';
COMMENT ON COLUMN PY_MONITOR.network_io_write_speed IS '网络写速度';
COMMENT ON COLUMN PY_MONITOR.disk_io_read_speed IS '磁盘读速度';
COMMENT ON COLUMN PY_MONITOR.disk_io_write_speed IS '磁盘写速度';
COMMENT ON COLUMN PY_MONITOR.cpu_percent IS 'cpu占用';
COMMENT ON COLUMN PY_MONITOR.create_date_time IS '创建时间';
COMMENT ON COLUMN PY_MONITOR.deleted IS '逻辑删除';
COMMENT ON COLUMN PY_MONITOR.tenant_id IS '租户id';

-- 596
-- standalone集群节点支持安装spark-local组件
ALTER TABLE PY_CLUSTER_NODE
  ADD COLUMN INSTALL_SPARK_LOCAL BOOLEAN DEFAULT FALSE;
COMMENT ON COLUMN PY_CLUSTER_NODE.INSTALL_SPARK_LOCAL IS '是否安装spark-local组件';

-- 931
-- 将ojdbc10-19.20.0.0.jar更新为ojdbc8-19.23.0.0.jar
UPDATE PY_DATABASE_DRIVER
SET ID        = 'ojdbc8-19.23.0.0',
    NAME      = 'ojdbc8-19.23.0.0',
    FILE_NAME = 'ojdbc8-19.23.0.0.jar'
WHERE ID LIKE 'oracle#_19.20.0.0';

-- 966
-- 添加下次执行时间
ALTER TABLE PY_WORKFLOW
  ADD COLUMN NEXT_DATE_TIME timestamp;
COMMENT ON COLUMN PY_WORKFLOW.NEXT_DATE_TIME IS '下一个日期时间';

-- 164
-- 添加消息体表
CREATE TABLE PY_MESSAGE
(
  id                      varchar(200) NOT NULL,
  name                    varchar(200) NOT NULL,
  status                  varchar(100) NOT NULL,
  remark                  varchar(500),
  msg_type                varchar(200) NOT NULL,
  msg_config              text         NOT NULL,
  response                text,
  create_by               varchar(200) NOT NULL,
  create_date_time        timestamp    NOT NULL,
  last_modified_by        varchar(200) NOT NULL,
  last_modified_date_time timestamp    NOT NULL,
  version_number          int          NOT NULL,
  deleted                 int          NOT NULL DEFAULT 0,
  tenant_id               varchar(200) NOT NULL,
  CONSTRAINT pk_py_message PRIMARY KEY (id)
);

-- 为列添加注释
COMMENT ON COLUMN PY_MESSAGE.id IS '消息消息体id';
COMMENT ON COLUMN PY_MESSAGE.name IS '消息体名称';
COMMENT ON COLUMN PY_MESSAGE.status IS '消息体状态';
COMMENT ON COLUMN PY_MESSAGE.remark IS '消息体备注';
COMMENT ON COLUMN PY_MESSAGE.msg_type IS '消息体类型，邮箱/阿里短信/飞书';
COMMENT ON COLUMN PY_MESSAGE.msg_config IS '消息体配置信息';
COMMENT ON COLUMN PY_MESSAGE.response IS '检测响应';
COMMENT ON COLUMN PY_MESSAGE.create_by IS '创建人';
COMMENT ON COLUMN PY_MESSAGE.create_date_time IS '创建时间';
COMMENT ON COLUMN PY_MESSAGE.last_modified_by IS '更新人';
COMMENT ON COLUMN PY_MESSAGE.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN PY_MESSAGE.version_number IS '版本号';
COMMENT ON COLUMN PY_MESSAGE.deleted IS '逻辑删除';
COMMENT ON COLUMN PY_MESSAGE.tenant_id IS '租户id';

-- 添加告警表
CREATE TABLE PY_ALARM
(
  id                      varchar(200) NOT NULL,
  name                    varchar(200) NOT NULL,
  status                  varchar(100) NOT NULL,
  remark                  varchar(500),
  alarm_type              varchar(200) NOT NULL,
  alarm_event             varchar(200) NOT NULL,
  msg_id                  varchar(100) NOT NULL,
  alarm_template          text         NOT NULL,
  receiver_list           text,
  create_by               varchar(200) NOT NULL,
  create_date_time        timestamp    NOT NULL,
  last_modified_by        varchar(200) NOT NULL,
  last_modified_date_time timestamp    NOT NULL,
  version_number          int          NOT NULL,
  deleted                 int          NOT NULL DEFAULT 0,
  tenant_id               varchar(200) NOT NULL,
  CONSTRAINT pk_py_alarm PRIMARY KEY (id)
);

-- 为列添加注释
COMMENT ON COLUMN PY_ALARM.id IS '告警id';
COMMENT ON COLUMN PY_ALARM.name IS '告警名称';
COMMENT ON COLUMN PY_ALARM.status IS '消息状态，启动/停止';
COMMENT ON COLUMN PY_ALARM.remark IS '告警备注';
COMMENT ON COLUMN PY_ALARM.alarm_type IS '告警类型，作业/作业流';
COMMENT ON COLUMN PY_ALARM.alarm_event IS '告警的事件，开始运行/运行失败/运行结束';
COMMENT ON COLUMN PY_ALARM.msg_id IS '使用的消息体';
COMMENT ON COLUMN PY_ALARM.alarm_template IS '告警的模版';
COMMENT ON COLUMN PY_ALARM.receiver_list IS '告警接受者';
COMMENT ON COLUMN PY_ALARM.create_by IS '创建人';
COMMENT ON COLUMN PY_ALARM.create_date_time IS '创建时间';
COMMENT ON COLUMN PY_ALARM.last_modified_by IS '更新人';
COMMENT ON COLUMN PY_ALARM.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN PY_ALARM.version_number IS '版本号';
COMMENT ON COLUMN PY_ALARM.deleted IS '逻辑删除';
COMMENT ON COLUMN PY_ALARM.tenant_id IS '租户id';

-- 添加消息告警实例表
CREATE TABLE PY_ALARM_INSTANCE
(
  id               varchar(200) NOT NULL,
  alarm_id         varchar(200) NOT NULL,
  send_status      varchar(100) NOT NULL,
  alarm_type       varchar(200) NOT NULL,
  alarm_event      varchar(200) NOT NULL,
  msg_id           varchar(100) NOT NULL,
  content          text         NOT NULL,
  response         text         NOT NULL,
  instance_id      varchar(200) NOT NULL,
  receiver         varchar(200) NOT NULL,
  send_date_time   timestamp    NOT NULL,
  create_date_time timestamp    NOT NULL,
  deleted          int          NOT NULL DEFAULT 0,
  tenant_id        varchar(200) NOT NULL,
  CONSTRAINT pk_py_alarm_instance PRIMARY KEY (id)
);

-- 为列添加注释
COMMENT ON COLUMN PY_ALARM_INSTANCE.id IS '告警实例id';
COMMENT ON COLUMN PY_ALARM_INSTANCE.alarm_id IS '告警id';
COMMENT ON COLUMN PY_ALARM_INSTANCE.send_status IS '是否发送成功';
COMMENT ON COLUMN PY_ALARM_INSTANCE.alarm_type IS '告警类型，作业/作业流';
COMMENT ON COLUMN PY_ALARM_INSTANCE.alarm_event IS '触发的事件';
COMMENT ON COLUMN PY_ALARM_INSTANCE.msg_id IS '告警的消息体';
COMMENT ON COLUMN PY_ALARM_INSTANCE.content IS '发送消息的内容';
COMMENT ON COLUMN PY_ALARM_INSTANCE.response IS '事件响应';
COMMENT ON COLUMN PY_ALARM_INSTANCE.instance_id IS '任务实例id';
COMMENT ON COLUMN PY_ALARM_INSTANCE.receiver IS '消息接受者';
COMMENT ON COLUMN PY_ALARM_INSTANCE.send_date_time IS '发送的时间';
COMMENT ON COLUMN PY_ALARM_INSTANCE.create_date_time IS '创建时间';
COMMENT ON COLUMN PY_ALARM_INSTANCE.deleted IS '逻辑删除';
COMMENT ON COLUMN PY_ALARM_INSTANCE.tenant_id IS '租户id';

-- 作业添加基线
ALTER TABLE PY_WORK_CONFIG
  ADD COLUMN ALARM_LIST TEXT;
COMMENT ON COLUMN PY_WORK_CONFIG.ALARM_LIST IS '绑定的基线';

-- 作业流添加基线
ALTER TABLE PY_WORKFLOW_CONFIG
  ADD COLUMN ALARM_LIST TEXT;
COMMENT ON COLUMN PY_WORKFLOW_CONFIG.ALARM_LIST IS '绑定的基线';

-- 作业版本添加基线
ALTER TABLE PY_WORK_VERSION
  ADD COLUMN ALARM_LIST TEXT;
COMMENT ON COLUMN PY_WORK_VERSION.ALARM_LIST IS '绑定的基线';

-- 作业流版本添加基线
ALTER TABLE PY_WORKFLOW_VERSION
  ADD COLUMN ALARM_LIST TEXT;
COMMENT ON COLUMN PY_WORKFLOW_VERSION.ALARM_LIST IS '绑定的基线';

-- 添加外部调用开关状态
ALTER TABLE PY_WORKFLOW_CONFIG
  ADD COLUMN INVOKE_STATUS VARCHAR(100) DEFAULT 'OFF' NOT NULL;

COMMENT ON COLUMN PY_WORKFLOW_CONFIG.INVOKE_STATUS IS '是否启动外部调用';

-- 新增excel同步配置字段
ALTER TABLE PY_WORK_CONFIG
    ADD COLUMN EXCEL_SYNC_CONFIG TEXT;

-- 实例表中添加EXCEL_SYNC_CONFIG字段
ALTER TABLE PY_WORK_VERSION
    ADD COLUMN EXCEL_SYNC_CONFIG TEXT;

-- 修复作业调度信息无法配置分钟级别配置
alter table py_work_config
    alter column cron_config type varchar(2000) using cron_config::varchar(2000);

-- 修复作业调度信息无法配置分钟级别配置
alter table py_workflow_version
    alter column cron_config type varchar(2000) using cron_config::varchar(2000);
        
-- 添加外部调用url
ALTER TABLE PY_WORKFLOW_CONFIG
    ADD COLUMN INVOKE_URL TEXT;

