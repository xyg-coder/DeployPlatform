#!/usr/bin/env bash

if git ls-remote $1 \&; then
    echo "is one repo"
else
    echo "not a repo"
fi