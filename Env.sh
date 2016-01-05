#!/usr/bin/env bash

BASEDIR="/home/tomek/dev/u2/sr/cloudalbum"
CLOUDALBUM_JAVA_OPTS="-Djava.security.debug=access,failure"
CLOUDALBUM_DEBUG_OPTS="-Djava.rmi.server.logCalls=true \
    -Dsun.rmi.server.logLevel=VERBOSE \
    -Dsun.rmi.client.logCalls=true \
    -Dsun.rmi.transport.tcp.logLevel=VERBOSE"
