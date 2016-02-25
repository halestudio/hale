#!/bin/bash

./build/gradlew -p build -Dorg.gradle.jvmargs="-XX:MaxPermSize=748M" -Dorg.gradle.daemon=true --stacktrace cli -Pargs="$*"
