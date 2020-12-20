alter table atherystowns.resident
add column if not exists isFake boolean default 0 not null;