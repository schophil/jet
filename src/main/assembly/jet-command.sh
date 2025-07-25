#!/bin/bash

if [ -f /tmp/jet.command ]; then
    command=$(cat /tmp/jet.command)
    rm /tmp/jet.command
    echo $command
    eval $command
fi

if [ -f /tmp/jet.copy ]; then
    command=$(cat /tmp/jet.copy)
    rm /tmp/jet.copy
    if uname -r | grep -q 'microsoft'; then
        echo "$command" | clip.exe
    elif command -v xsel >/dev/null 2>&1; then
        echo "$command" | xsel -i -b
    elif command -v wl-copy >/dev/null 2>&1; then
        echo "$command" | wl-copy
    fi
fi
