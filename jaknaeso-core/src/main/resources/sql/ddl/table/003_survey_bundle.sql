create table if not exists survey_bundle
(
    id         bigint auto_increment  primary key,
    created_at timestamp(6),
    deleted_at timestamp(6),
    updated_at timestamp(6)
);