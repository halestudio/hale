#!/bin/sh

# Default HALE builds
./build.sh clean
./build.sh product HALE -o linux -a x86_64
#./build.sh product HALE -o linux -a x86
./build.sh product HALE -o windows -a x86_64
#./build.sh product HALE -o windows -a x86
./build.sh product HALE -o macosx -a x86_64
