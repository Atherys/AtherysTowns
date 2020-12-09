create schema if not exists atherys;

create table if not exists atherys.Nation (
    id bigserial not null,
    bank uuid,
    color varchar(255),
    description varchar(255),
    joinable boolean not null,
    name varchar(255),
    tax float8 not null,
    version int4 not null,
    capital_id int8,
    leader_id uuid,
    primary key (id)
);

create table if not exists atherys.nation_allies (
    nation_id int8 not null,
    ally_nation_id int8 not null,
    primary key (nation_id, ally_nation_id)
);

create table if not exists atherys.nation_enemies (
    nation_id int8 not null,
    enemy_nation_id int8 not null,
    primary key (nation_id, enemy_nation_id)
);

create table if not exists atherys.NationPlot (
    id bigserial not null,
    neCorner varchar(255),
    swCorner varchar(255),
    version int4 not null,
    nation_id int8 not null,
    primary key (id)
);

create table if not exists atherys.Resident (
    id uuid not null,
    lastLogin timestamp,
    lastTownSpawn timestamp,
    name varchar(255),
    registeredOn timestamp,
    title varchar(255),
    version int4 not null,
    town_id int8,
    primary key (id)
);

create table if not exists atherys.resident_friends (
    resident_id uuid not null,
    friend_id uuid not null,
    primary key (resident_id, friend_id)
);

create table if not exists atherys.Resident_nationRoleIds (
    Resident_id uuid not null,
    nationRoleIds varchar(255)
);

create table if not exists atherys.Resident_townRoleIds (
    Resident_id uuid not null,
    townRoleIds varchar(255)
);

create table if not exists atherys.Town (
    id bigserial not null,
    bank uuid,
    color varchar(255),
    debt float8 not null,
    description varchar(255),
    freelyJoinable boolean not null,
    lastRaidCreationDate timestamp,
    lastTaxDate timestamp,
    maxSize int4 not null,
    motd varchar(255),
    name varchar(255),
    pvpEnabled boolean not null,
    spawn varchar(255),
    taxFailedCount int4 not null,
    taxable boolean not null,
    version int4 not null,
    world uuid,
    leader_id uuid,
    nation_id int8,
    primary key (id)
);

create table if not exists atherys.TownPlot (
    id bigserial not null,
    neCorner varchar(255),
    swCorner varchar(255),
    version int4 not null,
    name varchar(255),
    owner_id uuid,
    town_id int8 not null,
    primary key (id)
);

create table if not exists atherys.townplot_permissions (
    townplot_id int8 not null,
    townplot_permission_id int8 not null,
    primary key (townplot_id, townplot_permission_id)
);

create table if not exists atherys.TownPlotPermission (
    id bigserial not null,
    context varchar(255),
    worldPermission varchar(255),
    primary key (id)
);