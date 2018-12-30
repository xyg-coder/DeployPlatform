#!/usr/bin/env bash

# kill the process with the whole command
# $1: pattern

echo "grep $*"
ps -ef | grep "$*" | grep -v grep | cut -c 9-15 | xargs kill