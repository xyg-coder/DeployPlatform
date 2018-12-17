#!/usr/bin/env bash

# Defaultly read 500 last lines of the file dynamically
# Parameter:
# $1: file

if ! [ -f $1 ];
then
    exit 1;
fi
tail -f -n 500 $1