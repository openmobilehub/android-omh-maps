#!/bin/bash
mkdir -p ~/.gradle
echo "publishingSonatypeRepository=$1" >> ./local.properties
