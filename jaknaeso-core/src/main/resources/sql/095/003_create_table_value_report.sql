create table if not exists value_report
(
  id                  bigint        auto_increment primary key,
  keyword                           varchar(30),
  percentage                        decimal(5,2),
  character_record_id bigint        not null,
  created_at          timestamp(6),
  updated_at          timestamp(6),
  deleted_at          timestamp(6)
);