#!/usr/bin/env bash
localip=$(ip route get 8.8.8.8 | head -1 | cut -d' ' -f8)
echo "your local ip is $localip"
cd src
javac *.java
java -Djava.rmi.server.hostname="$localip" MainClient "$1"
