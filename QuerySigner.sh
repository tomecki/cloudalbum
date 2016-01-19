#!/usr/bin/env bash

set -v
CLOUDALBUM_DEBUG_OPTS="-Dsun.rmi.client.logCalls=true"
cd sr/cloudalbum
#cd cloudalbum
tmux ls | grep q:
if [ $? -eq 0 ]
then
    tmux send-keys -t q C-c C-m
    sleep 1
    tmux kill-session -t q
fi
tmux new -s q -d
tmux send-keys -t q 'source Env.sh' C-m
tmux send-keys -t q './Registry.sh' C-m
tmux send-keys -t q 'CLOUDALBUMPATH="$BASEDIR/out/production/cloudalbum/:$BASEDIR/lib/objenesis-2.1.jar:$BASEDIR/lib/kryo-shaded-3.0.0.jar:$BASEDIR/lib/minlog-1.3.0.jar:$BASEDIR/lib/cup.jar:$BASEDIR/lib/JLex.jar"' C-m
tmux send-keys -t q '$JAVA_ENV -classpath $CLOUDALBUMPATH \
    $CLOUDALBUM_JAVA_OPTS \
    $CLOUDALBUM_DEBUG_OPTS \
    -Djava.rmi.server.hostname=`hostname` \
    -Djava.rmi.server.codebase=file:$BASEDIR/out/production/cloudalbum/ \
    -Djava.security.policy=FetcherServer.policy \
	  pl.edu.mimuw.cloudalbum.querysigner.QuerySignerModule 1097' C-m
