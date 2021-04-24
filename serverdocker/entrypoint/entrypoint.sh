#!/usr/bin/env bash

echo "Going to server folder"
cd /app/server/DoorMonsterRestAPI
echo "building"
gradle build
echo "Starting with the server"
java -jar build/libs/gs-rest-service-0.1.0.jar --spring.profiles.active=prod