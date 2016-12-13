#!/usr/bin/env bash
localip=$(ip route get 8.8.8.8 | head -1 | cut -d' ' -f8)
echo "your local hostname is $localip"
cd src
for dir in ./*/ ; do
	echo "Compiling in package $dir..."
	javac -cp ".:sigar.jar" "$dir"/*.java
done
java -cp ".:sigar.jar" -Djava.rmi.server.hostname="$localip" client.MainClient "$1" "$localip"
