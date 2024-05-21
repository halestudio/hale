#!/usr/bin/env bash

ORG_DIR=$(pwd)
CURRENT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd ) # script directory
echo "Detected script directory as $CURRENT_DIR"

cd $CURRENT_DIR

set -e

./gradlew printVersion
./gradlew setRelease "-PnewVersion=$1"

# ideally all build tasks that don't publish would be done here to fail the release if it does not work
# but for the product build we include the revision information (commit sha), which is not available for the release commit yet

cd $ORG_DIR
