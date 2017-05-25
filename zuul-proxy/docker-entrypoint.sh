#!/bin/bash
#set -x

# change from default umask of 0022 to allow group writes
umask 0007

# SIGTERM-handler
term_handler() {
  echo "SIGTERM received"
  exit 143; # 128 + 15 -- SIGTERM
}

# SIGINT-handler
int_handler() {
  echo "SIGINT received"
  exit 130; # 128 + 2 -- SIGINT
}

# register signal handlers
trap 'term_handler' SIGTERM
trap 'int_handler' SIGINT

# first parameter '-jar' indicates regular startup
if [[ "$1" == "-jar" ]]; then
  # read JAVA_OPTS into arrays to avoid need for eval (and associated vulnerabilities)
  java_opts_array=()
  while IFS= read -r -d '' item; do
    java_opts_array+=( "$item" )
  done < <([[ $JAVA_OPTS ]] && xargs printf '%s\0' <<<"$JAVA_OPTS")

  exec java "${java_opts_array[@]}" "$@"
fi

# if there are params the user is likely trying to explore the image
exec "$@"
