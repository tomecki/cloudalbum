#!/usr/bin/env bash
cd sr/cloudalbum
git pull
if [ ! -f hosts ] 
then
    ./discoverHosts.sh > hosts
    ./generateZones.py < hosts
fi

ant
cat selectedHosts | cut -d ',' -f 1 | cut -d '/' -f 5 > hostbuffer

while read  nodehost
do
    echo "running singlenode script on "$nodehost
    ssh "td366732@"$nodehost < singlenode.sh
done < hostbuffer 
rm hostbuffer
