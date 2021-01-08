alter table atherystowns.resident
add column if not exists isFake boolean default false not null;