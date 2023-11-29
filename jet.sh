#!/bin/bash

DIR=$(dirname $(readlink -f $0))

java -jar $DIR/jet.jar

if [ -f /tmp/jet.command ]; then
    command=$(cat /tmp/jet.command)
    rm /tmp/jet.command
    $command
fi
