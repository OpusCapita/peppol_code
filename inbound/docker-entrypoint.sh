#!/bin/bash
#set -x

export CONFIGURATION_SERVER_HEALTH="http://configuration-server:8888/admin/health"
export MYSQL_HEALTH="http://mysql.docker.local:3306"
export RABBITMQ_HEALTH="http://rabbitmq.docker.local:15672"

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
  # check services before startup
  while ! curl -fs ${CONFIGURATION_SERVER_HEALTH} > /dev/null; do
      echo "$(date) - still waiting on ${CONFIGURATION_SERVER_HEALTH}";
      sleep 1;
  done
  
  while ! curl -fs ${MYSQL_HEALTH} > /dev/null; do
      echo "$(date) - still waiting on ${MYSQL_HEALTH}";
      sleep 1;
  done

  while ! curl -fs ${RABBITMQ_HEALTH} > /dev/null; do
      echo "$(date) - still waiting on ${RABBITMQ_HEALTH}";
      sleep 1;
  done
  
  # read JAVA_OPTS into arrays to avoid need for eval (and associated vulnerabilities)
  java_opts_array=()
  while IFS= read -r -d '' item; do
    java_opts_array+=( "$item" )
  done < <([[ $JAVA_OPTS ]] && xargs printf '%s\0' <<<"$JAVA_OPTS")

  exec java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 "${java_opts_array[@]}" "$@"
fi

# if there are params the user is likely trying to explore the image
exec "$@"
