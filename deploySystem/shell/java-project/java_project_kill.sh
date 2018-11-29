#!/usr/bin/env bash

# kill the running project
# parameters:
# $1: id

ps -ef | grep "java -cp target/uidIs$1.jar" | grep -v grep | cut -c 9-15 | xargs kill