# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups
ALTER TABLE "JOURNEY_LEGS" ADD "JOURNEY_ID" INTEGER NOT NULL;

# --- !Downs
ALTER TABLE "JOURNEY_LEGS" DROP "JOURNEY_NAME";