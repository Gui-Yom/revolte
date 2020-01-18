create type role as enum (
    'KING',
    'QUEEN',
    'WIZARD',
    'JESTER',
    'ASSASSIN',
    'TWIN',
    'REBEL',
    'REBEL_CHIEF',
    'DRUID',
    'FARSEER',
    'JUDGE',
    'HONEST_CITIZEN',
    'CITIZEN',
    'LOVER'
    );

create table games
(
    gameid   bigserial primary key,
    threadid varchar(20) unique,
    turn     smallint
);

create table players
(
    playerid bigserial primary key,
    psid     varchar(20) unique,
    health   smallint,
    role     role,
    gameid   bigserial references games (gameid),
    vote     bigserial references players (playerid)
);