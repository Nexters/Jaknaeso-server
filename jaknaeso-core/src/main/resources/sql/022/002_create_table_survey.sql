create table if not exists survey
(
    id          bigint auto_increment primary key,
    survey_type varchar(31) not null,
    content     varchar(255),
    bundle_id   bigint,
    created_at  timestamp(6),
    updated_at  timestamp(6),
    deleted_at  timestamp(6)
);