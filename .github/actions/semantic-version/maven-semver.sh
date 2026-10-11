#!/bin/sh
set -euf
export MAVEN_ARGS='--batch-mode --no-transfer-progress --quiet'

get_version() {
  version=$(mvn help:evaluate --define forceStdout --define expression=project.version)
  echo "$version"
}

next_version() {
  mvn build-helper:parse-version versions:set-property \
    --define property=revision --define newVersion="$1"
}

if [ "$#" -lt 2 ]; then
  exit 1
fi
cd "$2"

case "$1" in
get) ;;
major) next_version '${parsedVersion.nextMajorVersion}.0.0' ;;
minor) next_version '${parsedVersion.majorVersion}.${parsedVersion.nextMinorVersion}.0' ;;
patch) next_version '${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.nextIncrementalVersion}' ;;
*) exit 2 ;;
esac

version=$(get_version)
echo "$version"
