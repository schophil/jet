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
    # echo $command | xclip -selection clipboard
    echo $command | xsel -i -b
fi
