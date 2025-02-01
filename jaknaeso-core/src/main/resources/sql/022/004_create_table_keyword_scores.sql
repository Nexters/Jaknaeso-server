create table if not exists keyword_scores
(
    score            double,
    survey_option_id bigint not null,
    keyword ENUM('ACHIEVEMENT','BENEVOLENCE','CONFORMITY','HEDONISM','POWER','SECURITY','SELF_DIRECTION','STIMULATION','TRADITION','UNIVERSALISM'),
    FOREIGN KEY (survey_option_id) REFERENCES survey_option(id)
);