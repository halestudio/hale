#!/usr/bin/env bash

ORG_DIR=$(pwd)
CURRENT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd ) # script directory
echo "Detected script directory as $CURRENT_DIR"

cd $CURRENT_DIR

set -e

current_version=$(./gradlew printVersion | grep "Current version:" | sed "s/.*Current version: //")
echo "Current version: $current_version"

if [[ "$current_version" == *-SNAPSHOT ]]; then
  echo "Version is already $current_version — snapshot already set, nothing to do"
  cd $ORG_DIR
  exit 0
fi

./gradlew setSnapshot

git add -A
git commit -m "chore: use new snapshot version [skip ci]"
git push

cd $ORG_DIR
