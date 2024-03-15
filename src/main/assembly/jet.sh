#!/bin/bash

DIR=$(dirname $(readlink -f $0))

export LOGGING_FILE_NAME=logback-silent.xml
java -jar $DIR/jet.jar

if [ -f /tmp/jet.command ]; then
    command=$(cat /tmp/jet.command)
    rm /tmp/jet.command
    echo $command
    eval $command
fi

if [ -f /tmp/jet.copy ]; then
    command=$(cat /tmp/jet.copy)
    rm /tmp/jet.copy
    if command -v xsl >/dev/null 2>&1; then
        echo $command | xsel -i -b
    elif command -v wl-copy >/dev/null 2>&1; then
        echo $command | wl-copy
    fi
fi
