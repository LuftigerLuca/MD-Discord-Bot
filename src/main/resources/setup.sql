create table if not exists guilds (
    id varchar(255) not null,
    name varchar(255) not null,
    locale varchar(32) not null,

    primary key (id)
);

create table if not exists guild_members (
    guild_id varchar(32) not null,
    user_id varchar(32) not null,
    permissions varchar(255) not null,

    primary key (guild_id, user_id),
    foreign key (guild_id) references guilds (id)
);

create table if not exists guild_roles (
    guild_id varchar(32) not null,
    role_id varchar(32) not null,
    permissions varchar(255) not null,

    primary key (guild_id, role_id),
    foreign key (guild_id) references guilds (id)
);

create table if not exists guild_members_signe_offs(
    signed_off_id varchar(64) not null,
    guild_id varchar(32) not null,
    user_id varchar(32) not null,
    reason varchar(255) not null,
    from_date varchar(32) not null,
    to_date varchar(32) not null,
    accepted boolean not null,

    primary key (signed_off_id),
    foreign key (guild_id) references guilds (id)
);