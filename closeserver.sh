#!/usr/bin/env bash
ps -ef | grep rmiregistry | awk '{print $2}' | xargs kill
