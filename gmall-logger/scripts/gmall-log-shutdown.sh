#!/bin/bash
echo "Stopping SpringBoot Application [gmall-logger.jar] Starting...."
pid=`ps -ef | grep gmall-logger-1.0-SNAPSHOT.jar | grep -v grep | awk '{print $2}'`
if [ -n "$pid" ]
then
   echo "[[gmall-logger.jar]Force Kill -9 pid:" $pid
   kill -9 $pid
fi
echo "Stopping SpringBoot Application [gmall-logger.jar] Finished...."