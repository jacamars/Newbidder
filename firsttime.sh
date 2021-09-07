#!/bin/sh
#
docker-compose up -d

docker container stop rtbx_bidder_1

docker container stop rtbx_sqldb_1

docker-compose up -d

