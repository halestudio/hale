#!/bin/sh

if [ "$1" = "start" ] ; then
  mkdir -p logs
  ./@LAUNCHER >> logs/@LAUNCHER.out.log 2>&1 &
  echo "@LAUNCHER started in background. PID: $!"
  echo $! > @LAUNCHER.pid
elif [ "$1" = "stop" ] ; then
  shift
  if [ ! -r @LAUNCHER.pid ] ; then
    echo "@LAUNCHER.pid not found. It seems @LAUNCHER is not running."
    exit 1
  fi
  echo "Shutting down @LAUNCHER: `cat @LAUNCHER.pid`"
  if [ "$1" = "--force" -o "$1" = "-force" ] ; then
    kill -9 `cat @LAUNCHER.pid`
  else
    kill `cat @LAUNCHER.pid`
  fi
  rm @LAUNCHER.pid
else
  echo "Usage: @LAUNCHER.sh command"
  echo "Commands:"
  echo "  start         Start @LAUNCHER in the background"
  echo "  stop          Stop @LAUNCHER"
  echo "  stop --force  Stop @LAUNCHER (kill process)"
fi

