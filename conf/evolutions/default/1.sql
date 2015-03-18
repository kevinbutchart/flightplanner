# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "PLACES" ("NAME" VARCHAR(254) NOT NULL,"LATITUDE" DOUBLE PRECISION NOT NULL,"LONGITUDE" DOUBLE PRECISION NOT NULL,"ID" SERIAL NOT NULL PRIMARY KEY);

# --- !Downs

drop table "PLACES";

