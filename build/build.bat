@echo off
SetLocal EnableDelayedExpansion

set ARGS=%*
if NOT "%ARGS%" == "" (
  set ARGS=!ARGS:"=\"!
)

gradlew cli -Pargs="%ARGS%"
