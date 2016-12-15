FROM d-l-tools.ocnet.local:443/openjdk:8-peppol
MAINTAINER Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>

VOLUME "/etc/peppol2.0/eventing"
ENV EVENTING_HOME "/opt/local/services/peppol2.0/eventing"
ENV EVENTING_CONFIG "/etc/peppol2.0/eventing"

RUN \
    mkdir -p "${EVENTING_HOME}" && \
    mkdir -p "${EVENTING_CONFIG}" && \
    ln -s "${EVENTING_CONFIG}/" "${EVENTING_HOME}/config"

WORKDIR "${EVENTING_HOME}"
ADD ["build/libs/eventing.jar", "eventing.jar"]

CMD ["java", "$JAVA_OPTS", "-jar", "eventing.jar"]

