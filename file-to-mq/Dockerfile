FROM java:8-jre
MAINTAINER Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>

VOLUME "/etc/peppol2.0/file-to-mq"
ENV TRANSPORT_HOME "/opt/local/services/peppol2.0/file-to-mq"
ENV TRANSPORT_CONFIG "/etc/peppol2.0/file-to-mq"

RUN \
    mkdir -p "${TRANSPORT_HOME}" && \
    mkdir -p "${TRANSPORT_CONFIG}" && \
    ln -s "${TRANSPORT_CONFIG}/" "${TRANSPORT_HOME}/config"

WORKDIR "${TRANSPORT_HOME}"
ADD ["build/libs/file-to-mq.jar", "file-to-mq.jar"]

CMD ["java", "-jar", "file-to-mq.jar"]

