#!/bin/bash

./build/gradlew -p build -Dorg.gradle.jvmargs="-XX:MaxPermSize=460M" -Dorg.gradle.daemon=true --stacktrace cli -Pargs="$*"
