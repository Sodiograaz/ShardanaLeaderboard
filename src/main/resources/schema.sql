CREATE TABLE IF NOT EXISTS PLAYERS (
	uuid varchar(32) not null PRIMARY KEY,
	username varchar(32) not null
);

CREATE TABLE IF NOT EXISTS KILL_LEADERBOARD (
	uuid varchar(32) not null REFERENCES PLAYERS(uuid),
	kill_count int not null
);