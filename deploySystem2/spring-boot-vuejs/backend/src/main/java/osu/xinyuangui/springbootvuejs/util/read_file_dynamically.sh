#!/usr/bin/env bash

# Defautly read last 500 lines of the file dynamically
# Parameters:
# $1: file

if ! [ -f $1 ];
then
    exit 1;
fi
tail -f -n 500 $1