create table if not exists survey_submission
(
    id                 bigint auto_increment primary key,
    additional_comment varchar(255),
    member_id          bigint,
    survey_id          bigint,
    option_id          bigint,
    created_at         timestamp(6),
    updated_at         timestamp(6),
    deleted_at         timestamp(6)
);