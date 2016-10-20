FROM java:8-jre
MAINTAINER Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>

VOLUME "/etc/peppol2.0/transport"
ENV TRANSPORT_HOME "/opt/local/services/peppol2.0/transport"
ENV TRANSPORT_CONFIG "/etc/peppol2.0/transport"

RUN \
    mkdir -p "${TRANSPORT_HOME}" && \
    mkdir -p "${TRANSPORT_CONFIG}" && \
    ln -s "${TRANSPORT_CONFIG}/" "${TRANSPORT_HOME}/config"

WORKDIR "${TRANSPORT_HOME}"
ADD ["build/libs/transport.jar", "transport.jar"]

CMD ["java", "-jar", "transport.jar"]

