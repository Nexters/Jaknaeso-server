create table if not exists survey_submission
(
    id            bigint auto_increment   primary key,
    retrospective varchar(500),
    member_id     bigint                  not null,
    survey_id     bigint                  not null,
    option_id     bigint                  not null,
    submitted_at  timestamp(6),
    created_at    timestamp(6),
    updated_at    timestamp(6),
    deleted_at    timestamp(6)
);