delete from user_role;
delete from usr;
delete from book;

insert into book (title) values ('first book');

insert into usr (account_non_expired, account_non_locked, credentials_non_expired, enabled, password, username)
      values (true,true,true,true, '$2a$10$gyXi261WCdIhqEWBXFL4l./bglGc5zIN5wR8i.RSDQzxps0j7ZTBW', 'test_user');
insert into user_role (user_id, roles) values (select id from usr where username='test_user', 'USER');

insert into usr (account_non_expired, account_non_locked, credentials_non_expired, enabled, password, username)
    values (true,true,true,true, '$2a$10$gyXi261WCdIhqEWBXFL4l./bglGc5zIN5wR8i.RSDQzxps0j7ZTBW', 'test_admin');
insert into user_role (user_id, roles) values (select id from usr where username='test_admin', 'ADMIN');