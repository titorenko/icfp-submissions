#!/bin/bash

rm -fr results
mkdir results
mkdir results/lib
mkdir results/sources
cp play_icfp2015 results
cp Makefile results
cp README results
cp -r target/lib/* results/lib
cp target/icfp-2015.0.0-SNAPSHOT.jar results/lib
cp pom.xml results/sources
cp -r src results/sources
rm -fr results/sources/src/test
tar -czf hi-team.tar.gz -C results .

