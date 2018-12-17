#!/usr/bin/env bash

# start the java project
# parameters:
# $1: id
# $2: root_path(relative to the initial path)
# $3: mainClass

if ! [ -d $2 ];
then
    mkdir $2
    cd $2
    echo "run fail: no such folder" > nohup.out
    exit 1
fi
cd $2
nohup java -cp target/uidIs$1.jar $3 > nohup.out 2>&1