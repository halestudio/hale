#!/bin/bash
if [ -z $HALE_GRADLE_CONSOLE ]; then
  HALE_GRADLE_CONSOLE=auto
fi
./gradlew --console $HALE_GRADLE_CONSOLE --stacktrace cli -Pargs="$*"
