#!/usr/bin/env bash
tmux kill-session -t c
echo "running node from"`pwd`" on "`hostname`
cd sr/cloudalbum
./Registry.sh
tmux new -s c -d
tmux split-window -h -t c:0.0
tmux split-window -h -t c:0.1
tmux send-keys -t c:0.0 './FetcherServer.sh' C-m
tmux send-keys -t c:0.1 './FetcherClient.sh' C-m
tmux send-keys -t c:0.2 'ls' C-m


