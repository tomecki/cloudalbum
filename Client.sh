#!/usr/bin/env bash
source Env.sh
CLOUDALBUMPATH="$BASEDIR/out/production/cloudalbum/:$BASEDIR/lib/objenesis-2.1.jar:$BASEDIR/lib/kryo-shaded-3.0.0.jar:$BASEDIR/lib/minlog-1.3.0.jar:$BASEDIR/lib/cup.jar:$BASEDIR/lib/JLex.jar:$BASEDIR/lib/guava-19.0.jar"
$JAVA_ENV -classpath $CLOUDALBUMPATH \
    $CLOUDALBUM_JAVA_OPTS \
    $CLOUDALBUM_DEBUG_OPTS \
    -Djava.rmi.server.hostname=`hostname` \
    -Djava.rmi.server.codebase=file:$BASEDIR/out/production/cloudalbum/ \
    -Djava.security.policy=FetcherServer.policy \
	  pl.edu.mimuw.cloudalbum.client.GossipClient $@
