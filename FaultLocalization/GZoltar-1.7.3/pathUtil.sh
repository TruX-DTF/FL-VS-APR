#!/usr/bin/env bash

#
# Returns the test classpath of a previously checkout D4J's project-bug.
#
_get_test_classpath() {
  local USAGE="Usage: ${FUNCNAME[0]} <project_name> <bug_id>"
  if [ "$#" != 2 ]; then
    echo "$USAGE" >&2
    return 1
  fi

  local projectName="$1"
  local bugId="$2"

  cp=""

  if [ "$projectName" == "Math" ]; then
    cp="/target/test-classes"
  elif [ "$projectName" == "Time" ]; then
    if [ $bugId < 12 ]; then
      cp="/target/test-classes"
    else
      cp="/build/test"
    fi
  elif [ "$projectName" == "Lang" ]; then
    if [ $bugId <= 20 ]; then
      cp="/target/tests"
    elif [ $bugId <= 41 ]; then
      cp="/target/test-classes"
    else
      cp="/target/tests"
    fi
  elif [ "$projectName" == "Chart" ]; then
    cp="/build-tests"
  elif [ "$projectName" == "Closure" ]; then
    cp="/build/test"
  else # Mockito
    if [ $bugId <= 11 ]; then
      cp="/build/classes/test"
    elif [ $bugId <= 17 ]; then
      cp="/target/test-classes"
    elif [ $bugId <= 21 ]; then
      cp="/build/classes/test"
    else
      cp="/target/test-classes"
    fi
  fi

  echo "$cp"
  return 0
}

#
# Return full path of the target directory of source classes.
#
_get_src_classpath() {
  local USAGE="Usage: ${FUNCNAME[0]} <project_name> <bug_id>"
  if [ "$#" != 2 ]; then
    echo "$USAGE" >&2
    return 1
  fi

  local projectName="$1"
  local bugId="$2"

  cp=""

  if [ "$projectName" == "Math" ]; then
    cp="/target/classes"
  elif [ "$projectName" == "Time" ]; then
    if [ $bugId < 12 ]; then
      cp="/target/classes"
    else
      cp="/build/classes"
    fi
  elif [ "$projectName" == "Lang" ]; then
    cp="/target/classes"
  elif [ "$projectName" == "Chart" ]; then
    cp="/build"
  elif [ "$projectName" == "Closure" ]; then
    cp="/build/classes"
  else # Mockito
    if [ $bugId <= 11 ]; then
      cp="/build/classes/main"
    elif [ $bugId <= 17 ]; then
      cp="/target/classes"
    elif [ $bugId <= 21 ]; then
      cp="/build/classes/main"
    else
      cp="/target/classes"
    fi
  fi

  echo "$cp"
  return 0
}
