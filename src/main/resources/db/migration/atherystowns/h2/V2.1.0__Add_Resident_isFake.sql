alter table atherystowns.resident
add column if not exists isFake bit default 0 not null;