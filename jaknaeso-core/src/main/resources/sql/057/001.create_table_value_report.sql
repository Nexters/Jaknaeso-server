create table if not exists value_reports
(
  percentage    decimal(5,2),
  character_id  bigint        not null,
  keyword       varchar(30)
);