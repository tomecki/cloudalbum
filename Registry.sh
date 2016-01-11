#!/usr/bin/env bash




ps -efj | tr -s " " | grep td366732 | grep rmiregistry | grep 1097 | cut -d' ' -f 2
if [ $? -eq 0 ]
then
    echo "killing previous instance of rmiregistry"
    kill -9 `ps -efj | tr -s " " | grep td366732 | grep rmiregistry | grep 1097 | cut -d' ' -f 2`
fi

echo "running registry on "`hostname`

cd out/production/cloudalbum && rmiregistry 1097 &
