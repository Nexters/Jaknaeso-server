create table if not exists keyword_scores
(
    keyword          varchar(30),
    score            decimal(5,2),
    survey_option_id bigint         not null
);