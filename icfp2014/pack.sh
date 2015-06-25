#!/bin/bash

set -eu

submissionfile=solution-$1.tgz

echo "Submitting version $1 with sources"

echo "1. Compiling all files"

cd icfp
mvn clean install -DskipTests >/dev/null
if [ $? != 0 ] ; then
  echo "Failed to compile app."
  exit
fi
cd ..

echo "2. Creating directories"
rm -fr submission
mkdir submission
mkdir submission/solution
mkdir submission/code
mkdir submission/code/libs

echo "3. Copying binaries"
cp icfp/target/icfp-2014.0.0-SNAPSHOT.jar submission/code/libs/icfp-2014.jar
cp icfp/target/lib/* submission/code/libs

echo "4. Compiling programs"
java -classpath submission/code/libs/*:icfp/src/main/resources lambdaman.SubmissionCompiler /third_bot.gcc >submission/solution/lambdaman.gcc
java -classpath submission/code/libs/*:icfp/src/main/resources ghc.GHCLinker /ghost_seek_and_destroy.ghc false >submission/solution/ghost0.ghc
java -classpath submission/code/libs/*:icfp/src/main/resources ghc.GHCLinker /ghost_seek_and_destroy.ghc false >submission/solution/ghost1.ghc
java -classpath submission/code/libs/*:icfp/src/main/resources ghc.GHCLinker /ghost_seek_and_destroy2.ghc false >submission/solution/ghost2.ghc
java -classpath submission/code/libs/*:icfp/src/main/resources ghc.GHCLinker /ghost_protector.ghc false >submission/solution/ghost3.ghc

echo "5. Copying sources"
cp icfp/pom.xml submission/code
cp -r icfp/src submission/code
cp icfp/grammar.txt submission/code
cp icfp/README submission/code

echo "6. Preparing archive"
cd submission
tar -czf ../$submissionfile .
cd ..

echo
echo
echo "Team: hi, team"
echo "Language: java"
echo "Names:"
echo "Oleg Afanasiev"
echo "Konstantin Titorenko"
openssl dgst -sha1 $submissionfile


