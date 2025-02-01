create table if not exists keyword_scores
(
    score            double,
    survey_option_id bigint not null,
    keyword          varchar(30)
);