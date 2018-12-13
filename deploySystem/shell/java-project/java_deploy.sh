#!/usr/bin/env bash

# deploy the maven-based java project
# parameters
# $1: id of project
# $2: git url
# $3: root path(relative to initial path)

echo "rm -rf codes/deploy/$1"
rm -rf codes/deploy/$1

echo "mkdir -p codes/deploy/$1"
mkdir -p codes/deploy/$1

cd codes/deploy/$1
git clone $2
cd ../../..
cd $3

echo "mvn clean package"
mvn -Djar.finalName=uidIs$1 clean package > package_log.log

if ! [ -a target/uidIs$1.jar ]; then
    >&2 echo "build fail"
    exit 1
fi