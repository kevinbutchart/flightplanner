# --- !Ups

create table "JOURNEYS" ("NAME" VARCHAR(254) NOT NULL,"ID" SERIAL NOT NULL PRIMARY KEY);
create table "JOURNEY_LEGS" ("JOURNEY_ID" INTEGER NOT NULL,"FROM" INTEGER NOT NULL,"TO" INTEGER NOT NULL,"ID" SERIAL NOT NULL PRIMARY KEY);
create table "PLACES" ("NAME" VARCHAR(254) NOT NULL,"LATITUDE" DOUBLE PRECISION NOT NULL,"LONGITUDE" DOUBLE PRECISION NOT NULL,"ID" SERIAL NOT NULL PRIMARY KEY);
create table "METARSTATIONS" ("ICAO" VARCHAR(254) NOT NULL, "NAME" VARCHAR(254) NOT NULL, "LATITUDE" DOUBLE PRECISION NOT NULL,"LONGITUDE" DOUBLE PRECISION NOT NULL, "ELEVATION" INTEGER NOT NULL, "ID" SERIAL NOT NULL PRIMARY KEY);
create table "WINDS" ("ALTITUDE" INTEGER NOT NULL PRIMARY KEY,"DIR" INTEGER NOT NULL,"SPEED" INTEGER NOT NULL,"OAT" INTEGER NOT NULL);

insert into "PLACES" ("NAME", "LATITUDE", "LONGITUDE") values ('Amersham', 51.6797222222222, -0.600277777777778);
insert into "PLACES" ("NAME", "LATITUDE", "LONGITUDE") values ('Biggleswade', 52.0839722222222, -0.259944444444444);
insert into "PLACES" ("NAME", "LATITUDE", "LONGITUDE") values ('Bovingdon', 51.7285, -0.542361111111111);
insert into "PLACES" ("NAME", "LATITUDE", "LONGITUDE") values ('Cambridge Airport', 52.2045277777778, 0.174138888888889);
insert into "PLACES" ("NAME", "LATITUDE", "LONGITUDE") values ('Connington', 52.468060, -0.252177);
insert into "PLACES" ("NAME", "LATITUDE", "LONGITUDE") values ('Maple Cross', 51.62975, -0.504111111111111);
insert into "PLACES" ("NAME", "LATITUDE", "LONGITUDE") values ('St Giles', 51.6303888888889, -0.577166666666667);
insert into "PLACES" ("NAME", "LATITUDE", "LONGITUDE") values ('St Neots', 52.2285277777778, -0.267583333333333);
insert into "PLACES" ("NAME", "LATITUDE", "LONGITUDE") values ('Tring', 51.7984722222222, -0.660277777777778);
insert into "PLACES" ("NAME", "LATITUDE", "LONGITUDE") values ('Wing', 51.9043055555556, -0.750833333333333);
insert into "PLACES" ("NAME", "LATITUDE", "LONGITUDE") values ('Prices Risborough', 51.7228, -0.830125);
insert into "PLACES" ("NAME", "LATITUDE", "LONGITUDE") values ('Oakley', 51.7828333333, -1.0752861111);
insert into "PLACES" ("NAME", "LATITUDE", "LONGITUDE") values ('Little Horwood', 51.962708333, -0.870594444);
insert into "PLACES" ("NAME", "LATITUDE", "LONGITUDE") values ('Le Touquet', 50.518685, 1.620189);
insert into "PLACES" ("NAME", "LATITUDE", "LONGITUDE") values ('Cap Gris-Nez', 50.871632, 1.583275);
insert into "PLACES" ("NAME", "LATITUDE", "LONGITUDE") values ('Folkstone Lighthouse', 51.076022, 1.194844);
insert into "PLACES" ("NAME", "LATITUDE", "LONGITUDE") values ('Snodland', 51.326102, 0.440740);
insert into "PLACES" ("NAME", "LATITUDE", "LONGITUDE") values ('Stapleford', 51.653950, 0.155119);
insert into "PLACES" ("NAME", "LATITUDE", "LONGITUDE") values ('Cuffley', 51.710620, -0.115077);



insert into "WINDS" ("ALTITUDE", "DIR", "SPEED", "OAT") values (2000, 220, 20, 0);
insert into "WINDS" ("ALTITUDE", "DIR", "SPEED", "OAT") values (5000, 210, 20, -2);

insert into "JOURNEYS" ("NAME") values ('Unnamed')
# --- !Downs

drop table "WINDS";
drop table "PLACES";
drop table "JOURNEY_LEGS";
drop table "JOURNEYS";
drop table "METARSTATIONS"

