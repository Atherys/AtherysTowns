alter table atherystowns.resident
add column if not exists isFake bit default false not null;