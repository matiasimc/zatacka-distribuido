#!/usr/bin/env bash
cd src
javac *.java
java MainClient "$1"
