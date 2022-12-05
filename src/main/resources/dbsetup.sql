CREATE TABLE IF NOT EXISTS KillsAndDeathsTable
(
    uuid CHAR(40) DEFAULT "none" NOT NULL,
    kills INTEGER DEFAULT 0 NOT NULL,
    deaths INTEGER DEFAULT 0 NOT NULL,



    PRIMARY KEY (uuid)
    );
