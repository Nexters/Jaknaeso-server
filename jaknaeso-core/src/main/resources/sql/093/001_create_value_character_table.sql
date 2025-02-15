create table if not exists value_character
(
  id              bigint        auto_increment primary key,
  name            varchar(50),
  description     varchar(255),
  keyword         varchar(30)
);