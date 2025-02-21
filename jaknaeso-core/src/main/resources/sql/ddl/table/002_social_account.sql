create table if not exists social_account
(
    id              bigint auto_increment   primary key,
    oauth_id        varchar(255)  not null,
    social_provider varchar(10)   not null,
    member_id       bigint        not null,
    created_at      timestamp(6),
    updated_at      timestamp(6),
    deleted_at      timestamp(6)
);