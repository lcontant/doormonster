#!/bin/bash

set -e

cd /home/app/client/App/door-monster
echo "installing angular/cli"
npm install -g @angular/cli
echo "installing dependencies"
echo $(npm install)
echo "starting angular"
echo $(ng build --prod)
echo "starting nginx"
nginx -g "daemon off;"
echo "Started nginx"
