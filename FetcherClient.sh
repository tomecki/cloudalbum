#!/usr/bin/env bash
source Env.sh
BASEDIR="/home/tomek/dev/u2/sr/cloudalbum"
CLOUDALBUMPATH="$BASEDIR/out/production/cloudalbum/:$BASEDIR/lib/objenesis-2.1.jar:$BASEDIR/lib/kryo-shaded-3.0.0.jar:$BASEDIR/lib/minlog-1.3.0.jar"
java -classpath $CLOUDALBUMPATH \
    $CLOUDALBUM_JAVA_OPTS \
    $CLOUDALBUM_DEBUG_OPTS \
    -Djava.rmi.server.hostname=localhost \
    -Djava.rmi.server.codebase=file:/home/tomek/dev/u2/sr/cloudalbum/out/production/cloudalbum/ \
    -Djava.security.policy=FetcherServer.policy \
	  pl.edu.mimuw.cloudalbum.agent.Agent settings.conf
