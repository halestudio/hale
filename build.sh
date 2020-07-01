#!/bin/bash
if [ -z $HALE_GRADLE_CONSOLE ]; then
  HALE_GRADLE_CONSOLE=auto
fi
./build/gradlew -p build -Dorg.gradle.jvmargs="-XX:MaxPermSize=512M" --console $HALE_GRADLE_CONSOLE -Dorg.gradle.daemon=true --stacktrace cli -Pargs="$*"
