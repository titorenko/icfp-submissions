#!/bin/bash

LOG_FILE=regression.log

grep '^[0-9]\+ ' $LOG_FILE | awk 'BEGIN {total=0} END {print "Total score", total} {total=total+$1}'
 
