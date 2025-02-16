create table if not exists character_value_report
(
  id                   bigint        auto_increment primary key,
  character_record_id  bigint,
  created_at           timestamp(6),
  updated_at           timestamp(6),
  deleted_at           timestamp(6)
);