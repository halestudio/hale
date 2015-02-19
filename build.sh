#!/bin/bash

./build/gradlew -p build -Dorg.gradle.jvmargs="-XX:MaxPermSize=256M" -Dorg.gradle.daemon=true --stacktrace cli -Pargs="$*"
