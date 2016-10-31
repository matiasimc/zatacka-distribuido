#!/usr/bin/env bash

cd src
rmiregistry &
javac *.java
java MainServer "$1" "$2" "$3"
