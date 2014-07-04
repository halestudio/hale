@echo off
SetLocal EnableDelayedExpansion

set ARGS=%*
if NOT "%ARGS%" == "" (
  set ARGS=!ARGS:"=\"!
)

build\gradlew -p build --stacktrace cli -Pargs="%ARGS%"
