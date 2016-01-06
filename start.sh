#!/usr/bin/env bash
cd sr/cloudalbum
git pull
ant

hosts="violet04 yellow01"
for nodehost in $hosts
do
    ssh "td366732@"$nodehost < singlenode.sh
done

