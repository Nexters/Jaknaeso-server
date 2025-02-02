create table if not exists keyword_scores
(
    score            decimal(5,2),
    survey_option_id bigint not null,
    keyword          varchar(30)
);