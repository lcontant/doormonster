#! /bin/bash
DATE=`date '+%Y-%m-%d %H:%M:%S'`
mysqldump --user=root --password=2idCX73JzHUV3y9f whiteli4_wlhqdata_dev > /dumps/whiteli4_wlhqdata_dev$DATE.sql