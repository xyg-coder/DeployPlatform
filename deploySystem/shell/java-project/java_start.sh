#!/usr/bin/env bash

# start the java project
# parameters:
# $1: id
# $2: root_path
# $3: mainClass

cd codes/deploy/$1
cd $2
nohup java -cp target/uidIs$1.jar $3 > nohup.out 2>&1 &