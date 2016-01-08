#!/usr/bin/env bash
tmux kill-session -t c
echo "running node from"`pwd`" on "`hostname`
cd sr/cloudalbum
nmap -p1097 localhost | grep open

if [ $? -eq 1 ]
then
    ./Registry.sh
fi

tmux new -s c -d
tmux split-window -h -t c:0.0
tmux split-window -h -t c:0.1
tmux send-keys -t c:0.0 './FetcherServer.sh' C-m
sleep 2
tmux send-keys -t c:0.1 './Agent.sh conf/`hostname`' C-m
tmux send-keys -t c:0.2 'ls' C-m


