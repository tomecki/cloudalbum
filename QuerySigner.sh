#!/usr/bin/env bash
tmux new -s q -d
tmux send-keys -t q:0.0 'cd sr/cloudalbum' C-m
tmux send-keys -t q:0.0 'source Env.sh'
tmux send-keys -t q:0.0 'CLOUDALBUMPATH="$BASEDIR/out/production/cloudalbum/:$BASEDIR/lib/objenesis-2.1.jar:$BASEDIR/lib/kryo-shaded-3.0.0.jar:$BASEDIR/lib/minlog-1.3.0.jar:$BASEDIR/lib/cup.jar:$BASEDIR/lib/JLex.jar"' C-m
tmux send-keys -t q:0.0 '$JAVA_ENV -classpath $CLOUDALBUMPATH \
    $CLOUDALBUM_JAVA_OPTS \
    $CLOUDALBUM_DEBUG_OPTS \
    -Djava.rmi.server.hostname=localhost \
    -Djava.rmi.server.codebase=file:$BASEDIR/out/production/cloudalbum/ \
    -Djava.security.policy=FetcherServer.policy \
	  pl.edu.mimuw.cloudalbum.querysigner.QuerySignerModule' C-m
