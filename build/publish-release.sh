#!/usr/bin/env bash

ORG_DIR=$(pwd)
CURRENT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd ) # script directory
echo "Detected script directory as $CURRENT_DIR"

cd $CURRENT_DIR

set -e

# TODO: move these tasks also out to separate jobs to prevent a failure failing the release? Would allow to rerun the respective parts

#
# Build products
#

./build.sh product --arch x86_64 --os linux HALE

# Note: Windows installer is built in separate Job (on windows runner)
./build.sh product --arch x86_64 --os windows HALE

./build.sh product --arch x86_64 --os macosx HALE

./build.sh product --arch x86_64 --os linux --publish Infocenter

#
# Upload update site
#

./upload-site.sh


# Note: Deploying Maven artifacts is done in separate Job since it is currently prone to fail on GitHub Actions and should not fail the release


cd $ORG_DIR
