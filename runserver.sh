#!/usr/bin/env bash

ctrl_c() {
	echo ""
	echo "Closing server..."
	ps -ef | grep rmiregistry | awk '{print  $2}' | xargs kill
	exit
}

trap 'ctrl_c' SIGINT

if [ "$#" -ne 1 ] && [ "$#" -ne 3 ]
then
	echo "Bad number of arguments"
	exit
fi
cd src
rmiregistry &
for dir in ./*/ ; do
	echo "Compiling in package $dir..."
	javac "$dir"/*.java
done
if [ "$2" == "-n" ]
then
	java server.MainServer "$1" "$2" "$3"
else
	java server.MainServer "$1"
fi
ctrl_c
