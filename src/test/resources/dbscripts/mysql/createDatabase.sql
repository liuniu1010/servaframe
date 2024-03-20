--this scripts should be executed with root(mysql account)
create database testenv;
grant all privileges on testenv.* to 'testenv'@'%' identified by 'abcd1234';
grant all privileges on testenv.* to 'testenv'@'localhost' identified by 'abcd1234';
