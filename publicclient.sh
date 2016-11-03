#!/usr/bin/env bash
publicip=$(dig +short myip.opendns.com @resolver1.opendns.com)
echo "your public ip is $publicip"
cd src
javac *.java
java -Djava.rmi.server.hostname="$publicip" MainClient "$1"
