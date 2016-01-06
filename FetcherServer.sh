#!/usr/bin/env bash
source Env.sh
java -classpath $BASEDIR/out/production/cloudalbum/ \
    $CLOUDALBUM_JAVA_OPTS \
    $CLOUDALBUM_DEBUG_OPTS \
    -Djava.rmi.server.hostname=localhost \
    -Djava.rmi.server.codebase=file:$BASEDIR/out/production/cloudalbum/ \
    -Djava.security.policy=FetcherServer.policy \
    pl.edu.mimuw.cloudalbum.fetcher.FetcherModule 1097
