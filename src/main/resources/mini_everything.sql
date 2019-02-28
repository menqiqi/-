-- 创建数据库
-- create database if not exists mini_everything;
-- 创建数据库表
drop table if exists thing;
create table if not exists thing(
  name varchar (256) not null comment '文件名称',
  path varchar (2048) not null comment '文件路径',
  depth int not null comment '文件路径深度',
  file_type varchar (32) not null comment '文件类型'
);
