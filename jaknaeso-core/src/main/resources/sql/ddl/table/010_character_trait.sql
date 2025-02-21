create table if not exists character_trait
(
    id                 bigint auto_increment  primary key,
    value_character_id bigint unsigned        not null,
    type               varchar(20)            not null,
    description        varchar(255)           not null,
    created_at         timestamp(6),
    updated_at         timestamp(6),
    deleted_at         timestamp(6)
);