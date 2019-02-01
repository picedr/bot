-- Table: agenda_entry

-- DROP TABLE agenda_entry;

CREATE TABLE agenda_entry
(
  id integer NOT NULL DEFAULT nextval('agenda_entry_id_seq'::regclass),
  date timestamp without time zone,
  content text,
  type text,
  serverid text,
  CONSTRAINT agenda_entry_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE agenda_entry
  OWNER TO yazz;


-- Table: agenda_rappel

-- DROP TABLE agenda_rappel;

CREATE TABLE agenda_rappel
(
  id integer NOT NULL DEFAULT nextval('agenda_rappel_id_seq'::regclass),
  date timestamp without time zone,
  parent integer,
  CONSTRAINT agenda_rappel_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE agenda_rappel
  OWNER TO yazz;



-- Table: botroles

-- DROP TABLE botroles;

CREATE TABLE botroles
(
  userid text NOT NULL,
  role text NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.botroles
  OWNER TO yazz;



-- Table: commands

-- DROP TABLE commands;

CREATE TABLE commands
(
  id integer NOT NULL DEFAULT nextval('command_id_seq'::regclass),
  serverid text,
  name text,
  value text,
  admin boolean,
  CONSTRAINT commands_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE commands
  OWNER TO yazz;


-- Table: confbot

-- DROP TABLE confbot;

CREATE TABLE confbot
(
  name text NOT NULL,
  value text,
  CONSTRAINT confbot_pkey PRIMARY KEY (name)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE confbot
  OWNER TO yazz;


-- Table: confserver

-- DROP TABLE confserver;

CREATE TABLE confserver
(
  serverid text NOT NULL,
  name text NOT NULL,
  value text
)
WITH (
  OIDS=FALSE
);
ALTER TABLE confserver
  OWNER TO yazz;


-- Table: flood

-- DROP TABLE flood;

CREATE TABLE flood
(
  serverid text NOT NULL,
  userid text NOT NULL,
  score integer NOT NULL,
  niv integer NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE flood
  OWNER TO yazz;


-- Table: mute

-- DROP TABLE mute;

CREATE TABLE mute
(
  serverid text NOT NULL,
  userid text NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE mute
  OWNER TO yazz;


-- Table: pfc

-- DROP TABLE pfc;

CREATE TABLE pfc
(
  userid text NOT NULL,
  win integer NOT NULL,
  draw integer NOT NULL,
  lose integer NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE pfc
  OWNER TO yazz;


-- Table: pimp

-- DROP TABLE pimp;

CREATE TABLE pimp
(
  serverid text NOT NULL,
  userid text NOT NULL,
  score integer NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE pimp
  OWNER TO yazz;


-- Table: questionquizz

-- DROP TABLE questionquizz;

CREATE TABLE questionquizz
(
  id integer NOT NULL DEFAULT nextval('questionquizz_id_seq'::regclass),
  serverid text NOT NULL,
  key text NOT NULL,
  question text NOT NULL,
  answer text NOT NULL,
  retour text NOT NULL,
  CONSTRAINT questionquizz_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE questionquizz
  OWNER TO yazz;


-- Table: rite

-- DROP TABLE rite;

CREATE TABLE rite
(
  userid text,
  state integer,
  lasthelp timestamp without time zone,
  help text
)
WITH (
  OIDS=FALSE
);
ALTER TABLE rite
  OWNER TO yazz;


-- Table: services

-- DROP TABLE services;

CREATE TABLE services
(
  serverid text NOT NULL,
  servicename text NOT NULL,
  state text NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE services
  OWNER TO yazz;


-- Table: shame

-- DROP TABLE shame;

CREATE TABLE public.shame
(
  serverid text NOT NULL,
  userid text NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE shame
  OWNER TO yazz;


-- Table: slow

-- DROP TABLE slow;

CREATE TABLE slow
(
  channelid text NOT NULL,
  delay integer NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE slow
  OWNER TO yazz;


-- Table: userquizz

-- DROP TABLE userquizz;

CREATE TABLE userquizz
(
  questionid integer NOT NULL,
  userid text NOT NULL,
  state integer NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE userquizz
  OWNER TO yazz;





