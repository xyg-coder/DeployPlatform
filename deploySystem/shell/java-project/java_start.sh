#!/usr/bin/env bash

# start the java project
# parameters:
# $1: id
# $2: root_path(relative to the initial path)
# $3: mainClass

cd $2
nohup java -cp target/uidIs$1.jar $3 > nohup.out 2>&1