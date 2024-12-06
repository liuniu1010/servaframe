create table employee (
id                 char(36)           not null primary key,
version            int                not null,
no                 varchar(10)        not null unique,
name               varchar(30),
age                int,
gender             varchar(10),
phone              varchar(20),
address            varchar(100),
createdate         date
) engine=innodb default charset=utf8mb4;

