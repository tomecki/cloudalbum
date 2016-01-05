#!/usr/bin/bash
source Env.sh
java -classpath /home/tomek/dev/u2/sr/cloudalbum/out/production/cloudalbum/ \
    $CLOUDALBUM_JAVA_OPTS \
    $CLOUDALBUM_DEBUG_OPTS \
    -Djava.rmi.server.hostname=localhost \
    -Djava.rmi.server.codebase=file:/home/tomek/dev/u2/sr/cloudalbum/out/production/cloudalbum/ \
    -Djava.security.policy=FetcherServer.policy \
	  pl.edu.mimuw.cloudalbum.fetcher.FetcherModule
