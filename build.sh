#!/bin/bash

./build/gradlew -p build --stacktrace cli -Pargs="$*"
