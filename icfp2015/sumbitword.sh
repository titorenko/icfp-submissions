#!/bin/bash

SEQ=$1
TAG=word-check

[ -z "$SEQ" ] && echo "Provide seq arg" && exit -1

curl --user :w0agR+pfoRAqLcowQmQSmM8VbLuQGGvb4cdqqpv6A2Y= -X POST -ssl -H "Content-Type: application/json" -d "[{ \"problemId\":0 , \"seed\":0, \"tag\": \"$TAG\", \"solution\": \"$SEQ\" }]" https://davar.icfpcontest.org/teams/238/solutions

echo
echo Done

