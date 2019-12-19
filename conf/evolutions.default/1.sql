# --- !Ups

create table users (
    id bigint not null auto_increment,
    username char(100) not null,
    password char(100) not null,
    primary key(id),
    unique(username)
);

# --- !Downs

drop table if exists users;
