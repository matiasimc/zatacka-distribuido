#!/usr/bin/env bash
publicip=$(dig +short myip.opendns.com @resolver1.opendns.com)
echo "your public hostname is $publicip"
cd src
for dir in ./*/ ; do
	echo "Compiling in package $dir..."
	javac "$dir"/*.java
donejavac *.java
java -Djava.rmi.server.hostname="$publicip" client.MainClient "$1"
