#!/bin/bash

nohup java -Xmx64m -Xms32m \
	-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=38081 \
	-jar gmall-logger-1.0-SNAPSHOT.jar > ./logs/gmall-logger.log 2>&1 &