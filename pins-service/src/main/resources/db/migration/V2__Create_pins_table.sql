CREATE TABLE "pins" (
    "id" SERIAL,
    "type" VARCHAR NOT NULL,
    "jid" VARCHAR NOT NULL,
    "lat" DOUBLE PRECISION NOT NULL,
    "long" DOUBLE PRECISION NOT NULL,
    "person_id" INT NOT NULL
)