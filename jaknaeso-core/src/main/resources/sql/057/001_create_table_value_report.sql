create table if not exists value_reports
(
  percentage                decimal(5,2),
  keyword                   varchar(30),
  character_value_report_id  bigint        not null
);