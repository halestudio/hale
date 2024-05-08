#!/usr/bin/env bash

ORG_DIR=$(pwd)
CURRENT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd ) # script directory
echo "Detected script directory as $CURRENT_DIR"

cd $CURRENT_DIR

set -e

./gradlew printVersion
./gradlew setSnapshot

git add -A
git commit -m "chore: use new snapshot version [skip ci]"
git push

cd $ORG_DIR
