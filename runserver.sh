#!/bin/bash

./finish.sh
./compile.sh
cd src
java MainServer "$1" "$2" "$3"
