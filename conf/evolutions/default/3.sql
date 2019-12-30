# --- !Ups
insert into users (username, password) values ('admin', 'admin'); #типа админка

# --- !Downs
DELETE FROM users WHERE username = 'admin'