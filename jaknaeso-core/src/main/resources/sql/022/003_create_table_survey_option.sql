create table if not exists survey_option
(
    id         bigint auto_increment primary key,
    content    varchar(255),
    survey_id  bigint,
    created_at timestamp(6),
    updated_at timestamp(6),
    deleted_at timestamp(6),
    FOREIGN KEY (survey_id) REFERENCES survey(id)
);
