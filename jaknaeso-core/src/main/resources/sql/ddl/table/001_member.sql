create table if not exists member
(
    id                      bigint auto_increment primary key,
    email                   varchar(255),
    name                    varchar(60),
    completed_onboarding_at timestamp(6),
    created_at              timestamp(6),
    updated_at              timestamp(6),
    deleted_at              timestamp(6)
);