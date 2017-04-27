#!/bin/bash
#set -x

export CONFIGURATION_SERVER_HEALTH="http://configuration-server:8888/admin/health"
export RABBITMQ_HEALTH="http://configuration-server:8888/admin/health"

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

# check services before startup
while ! curl -fs ${CONFIGURATION_SERVER_HEALTH} > /dev/null; do
    echo "$(date) - still waiting on ${CONFIGURATION_SERVER_HEALTH}";
    sleep 1;
done

while ! curl -fs ${RABBITMQ_HEALTH} > /dev/null; do
    echo "$(date) - still waiting on ${RABBITMQ_HEALTH}";
    sleep 1;
done

# run the command
java $JAVA_OPTS -jar ${TRANSPORT_HOME}/mq-to-file.jar
