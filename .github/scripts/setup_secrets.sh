#!/bin/bash
mkdir -p ~/.gradle
echo "MAPBOX_DOWNLOADS_TOKEN=$1" >> ~/.gradle/gradle.properties
echo "MAPS_API_KEY=$2" >> ./local.properties
echo "MAPBOX_PUBLIC_TOKEN=$3" >> ./local.properties
echo "AZURE_MAPS_SUBSCRIPTION_KEY=$4" >> ./local.properties
