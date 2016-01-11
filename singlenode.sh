#!/usr/bin/env bash
set -v
echo "running node from"`pwd`" on "`hostname`


# previous session cleanup
tmux ls | grep c:
if [ $? -eq 0 ]
then
    tmux send-keys -t c:0.0 C-c C-m
    tmux send-keys -t c:0.1 C-c C-m
    sleep 1
    tmux kill-session -t c
fi

cd sr/cloudalbum

# checking for rmiregistry

echo "starting tmux session on "`hostname`
tmux new -s c -d
tmux split-window -h -t c:0.0
tmux split-window -h -t c:0.1
tmux send-keys -t c:0.0 './Registry.sh' C-m
tmux send-keys -t c:0.0 './FetcherServer.sh' C-m
sleep 2
tmux send-keys -t c:0.1 './Agent.sh conf/`hostname`' C-m
tmux send-keys -t c:0.2 'ls' C-m
echo "tmux session routine on "`hostname`"finished"
exit
