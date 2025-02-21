create table if not exists withdrawn_member
(
    id                  bigint        auto_increment primary key,
    withdrawn_at        timestamp(6),
    email               varchar(255),
    name                varchar(60)
);