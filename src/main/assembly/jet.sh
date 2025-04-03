#!/bin/bash

DIR=$(dirname $(readlink -f $0))

export LOGGING_FILE_NAME=logback-silent.xml
java -Dapp.bin=$DIR -jar $DIR/jet.jar

./jet-command.sh
