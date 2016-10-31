#!/usr/bin/env bash

cd src
rmiregistry &
javac *.java
if [ "$2" == "-n" ]
then
java MainServer "$1" "$2" "$3"
else
java MainServer "$1"
fi
