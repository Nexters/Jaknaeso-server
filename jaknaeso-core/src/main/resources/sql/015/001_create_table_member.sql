create table if not exists member
(
  id              bigint        auto_increment primary key,
  created_at      timestamp(6),
  updated_at      timestamp(6),
  deleted_at      timestamp(6)
);