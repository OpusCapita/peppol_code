FROM d-l-tools.ocnet.local:443/openjdk:8-peppol
MAINTAINER Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>

VOLUME "/etc/peppol2.0/validator"
ENV VALIDATOR_HOME "/opt/local/services/peppol2.0/validator"
ENV VALIDATOR_CONFIG "/etc/peppol2.0/validator"

RUN \
    mkdir -p "${VALIDATOR_HOME}" && \
    mkdir -p "${VALIDATOR_CONFIG}" && \
    ln -s "${VALIDATOR_CONFIG}/" "${VALIDATOR_HOME}/config"

WORKDIR "${VALIDATOR_HOME}"
ADD ["build/libs/validator.jar", "validator.jar"]

CMD java $JAVA_OPTS -jar validator.jar
EXPOSE 8080
