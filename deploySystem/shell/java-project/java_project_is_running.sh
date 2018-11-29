#!/usr/bin/env bash

# check if one project is running
# parameters:
# $1: id

ps -ef | grep "java -cp target/uidIs$1.jar" | grep -v grep