#!/bin/bash

shutdown_server() {
  local PIDFILE=$1
  local FORCE=$2
  
  # check for process with this PID
  local PID=`cat $PIDFILE`
  local PROCS=`ps -A | grep @LAUNCHER | grep $PID | wc -l`
  if [ $PROCS = 0 ] ; then
    # there is no process with this PID
    echo "Deleting orphaned PID file: $PID"
    rm $PIDFILE
    return
  fi
  
  # shutdown process with this PID
  echo "Shutting down @LAUNCHER: $PID"
  if [ "$FORCE" = "--force" -o "$FORCE" = "-force" ] ; then
    echo "Forced shutdown!"
    kill -9 $PID
  else
    kill $PID
  fi
  
  # wait for the process to exit
  sleep 1
  local SECONDS=0
  local TIMEOUT=30
  while ps -p $PID > /dev/null; do
    if [ $SECONDS = 0 ] ; then
      echo -n "Waiting $TIMEOUT seconds ..."
    fi
    let SECONDS=SECONDS+1
    sleep 1
    echo -n "."
    if [ $SECONDS = $TIMEOUT ] ; then
      echo ""
      echo "@LAUNCHER did not exit after $TIMEOUT seconds. Forcing shutdown!"
      kill -9 $PID
      break
    fi
  done
  if ([ ! $SECONDS = 0 ] && [ ! $SECONDS = $TIMEOUT ]) ; then
    echo ""
  fi
  
  # remove PID file
  rm $PIDFILE
}

if [ "$1" = "start" ] ; then
  mkdir -p logs
  ./@LAUNCHER >> logs/@LAUNCHER.out.log 2>&1 &
  echo "@LAUNCHER started in background. PID: $!"
  echo $! > "@LAUNCHER_$!.pid"
elif [ "$1" = "stop" ] ; then
  shift
  
  # find all .pid files
  COUNT=0
  PIDFILES=`find . -name '*.pid'`
  for f in $PIDFILES ; do
    # shutdown each server instance
    shutdown_server "$f" "$1"
    let COUNT=COUNT+1
  done
  
  if [ $COUNT = 0 ] ; then
    echo "No pid file found. It seems @LAUNCHER is not running."
    exit 1
  fi
else
  echo "Usage: @LAUNCHER.sh command"
  echo "Commands:"
  echo "  start         Start @LAUNCHER in the background"
  echo "  stop          Stop @LAUNCHER"
  echo "  stop --force  Stop @LAUNCHER (kill process)"
fi

