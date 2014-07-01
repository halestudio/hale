@echo off
SetLocal EnableDelayedExpansion

set ARGS=%*
if NOT "%ARGS%" == "" (
  set ARGS=!ARGS:"=\"!
)

gradlew --stacktrace cli -Pargs="%ARGS%"
