#!/usr/bin/env bash




ps -efj | tr -s " " | grep td366732 | grep rmiregistry | grep 1097 | cut -d' ' -f 2
if [ $? -eq 1 ]
then
    echo "running registry on "`hostname`

    cd out/production/cloudalbum && rmiregistry 1097 &

    #echo "killing previous instance of rmiregistry"
    #kill -9 `ps -efj | tr -s " " | grep td366732 | grep rmiregistry | grep 1097 | cut -d' ' -f 2`
fi


