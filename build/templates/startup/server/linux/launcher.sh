#!/bin/bash

shutdown_server() {
  local PIDFILE=\$1
  local FORCE=\$2
  
  # check for process with this PID
  local PID=`cat \$PIDFILE`
  local PROCS=`ps -A | grep ${launcher} | grep \$PID | wc -l`
  if [ \$PROCS = 0 ] ; then
    # there is no process with this PID
    echo "Deleting orphaned PID file: \$PID"
    rm \$PIDFILE
    return
  fi
  
  # shutdown process with this PID
  echo "Shutting down ${launcher}: \$PID"
  if [ "\$FORCE" = "--force" -o "\$FORCE" = "-force" ] ; then
    echo "Forced shutdown!"
    kill -9 \$PID
  else
    kill \$PID
  fi
  
  # wait for the process to exit
  sleep 1
  local SECONDS=0
  local TIMEOUT=30
  while ps -p \$PID > /dev/null; do
    if [ \$SECONDS = 0 ] ; then
      echo -n "Waiting \$TIMEOUT seconds ..."
    fi
    let SECONDS=SECONDS+1
    sleep 1
    echo -n "."
    if [ \$SECONDS = \$TIMEOUT ] ; then
      echo ""
      echo "${launcher} did not exit after \$TIMEOUT seconds. Forcing shutdown!"
      kill -9 \$PID
      break
    fi
  done
  if ([ ! \$SECONDS = 0 ] && [ ! \$SECONDS = \$TIMEOUT ]) ; then
    echo ""
  fi
  
  # remove PID file
  rm \$PIDFILE
}

if [ "\$1" = "start" ] ; then
  mkdir -p logs
  ./${launcher} >> logs/${launcher}.out.log 2>&1 &
  echo "${launcher} started in background. PID: \$!"
  echo \$! > "${launcher}_\$!.pid"
elif [ "\$1" = "stop" ] ; then
  shift
  
  # find all .pid files
  COUNT=0
  PIDFILES=`find . -name '*.pid'`
  for f in \$PIDFILES ; do
    # shutdown each server instance
    shutdown_server "\$f" "\$1"
    let COUNT=COUNT+1
  done
  
  if [ \$COUNT = 0 ] ; then
    echo "No pid file found. It seems ${launcher} is not running."
    exit 1
  fi
else
  echo "Usage: ${launcher}.sh command"
  echo "Commands:"
  echo "  start         Start ${launcher} in the background"
  echo "  stop          Stop ${launcher}"
  echo "  stop --force  Stop ${launcher} (kill process)"
fi

