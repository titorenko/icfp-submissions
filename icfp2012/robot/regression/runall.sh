#!/bin/bash

LOG_FILE=regression.log
LOG_CONFIG=log4j-silent.xml
GAME_TIMEOUT=150
MAP_DIR=../mines

files=`ls $MAP_DIR`

echo Started at `date` >$LOG_FILE

for map in $files ; do
  echo "Map $map"
  classpath=../lib/log4j-1.2.17.jar:../lib/slf4j-api-1.6.6.jar:../lib/slf4j-log4j12-1.6.6.jar:../out/icfpc-hiteam.jar
  echo "For map $map" >>$LOG_FILE
  java -Dgame.timeout=$GAME_TIMEOUT -Dlog4j.configuration=$LOG_CONFIG -classpath $classpath -Dregression.mode runner.BotRunner <$MAP_DIR/$map >>$LOG_FILE &
  java_pid=$!
  echo "Started java process with pid $java_pid"
  sleep $(($GAME_TIMEOUT+10))
  kill -9 $java_pid >/dev/null 
done

echo Finished at `date` >>$LOG_FILE

