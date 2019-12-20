# --- !Ups

create table tasks (
    id bigint not null auto_increment,
    userId bigint not null,
    text text not null,
    done bool,
    deleted bool,
    primary key(id)
);

# --- !Downs

drop table if exists tasks;