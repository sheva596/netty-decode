create table log
(
    id               bigint auto_increment
        primary key,
    create_time      datetime default CURRENT_TIMESTAMP null,
    description      varchar(255)                       null,
    username         varchar(255)                       not null,
    params           text                               null,
    request_ip       varchar(255)                       null,
    log_type         varchar(255)                       not null,
    exception_detail text                               null,
    method           varchar(255)                       not null,
    execute_time     bigint                             null
)
    comment '系统日志' charset = utf8;

create table req_record
(
    id           bigint auto_increment
        primary key,
    sn           varchar(255) not null,
    ip           varchar(255) not null,
    req_data     text         not null,
    rsp_data     text         not null,
    execute_time bigint       not null
)
    comment '请求记录' charset = utf8;

create table terminal
(
    id        bigint auto_increment
        primary key,
    sn        varchar(255)     not null,
    req_times bigint default 0 not null
)
    comment '终端记录' charset = utf8;

create table user
(
    id       int(11) unsigned auto_increment
        primary key,
    username varchar(255) not null,
    password varchar(255) not null
)
    comment '用户信息' charset = utf8;
