create table if not exists character_record
(
    id                  bigint auto_increment primary key,
    character_no        varchar(10),
    ordinal_number      integer               not null,
    start_date          date                  not null,
    end_date            date                  not null,
    value_character_id  bigint                not null,
    bundle_id           bigint                not null,
    member_id           bigint                not null,
    created_at          timestamp(6),
    updated_at          timestamp(6),
    deleted_at          timestamp(6)
);