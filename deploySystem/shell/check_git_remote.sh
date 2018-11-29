#!/usr/bin/env bash

# Check if one remote repo is valid
# parameter:
# $1: repo url

if git ls-remote $1 \&; then
    echo "is one repo"
else
    echo "not a repo"
fi