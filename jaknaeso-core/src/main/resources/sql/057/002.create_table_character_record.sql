create table if not exists character_record
(
  id              bigint        auto_increment primary key,
  character_no    varchar(10),
  start_date      date,
  end_date        date,
  bundle_id       bigint,
  created_at      timestamp(6),
  updated_at      timestamp(6),
  deleted_at      timestamp(6)
);