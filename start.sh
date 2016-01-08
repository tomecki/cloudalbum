#!/usr/bin/env bash
cd sr/cloudalbum
git pull
if [ ! -f hosts ] 
then
    ./discoverHosts.sh > hosts
    ./generateZones.py < hosts
fi

ant

while read nodehost
do
    ssh "td366732@"$nodehost < singlenode.sh
done < selectedHosts

