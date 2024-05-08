#!/usr/bin/env bash

ORG_DIR=$(pwd)
CURRENT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd ) # script directory
echo "Detected script directory as $CURRENT_DIR"

cd $CURRENT_DIR

set -e

# TODO perform all release tasks
# XXX for now only build one product
./build.sh product HALE -o linux -a x86_64

# ./upload-site.sh

cd $ORG_DIR
