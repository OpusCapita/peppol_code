FROM d-l-tools.ocnet.local:443/openjdk:8-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

VOLUME "/etc/peppol2.0/mq-to-file"
ENV TRANSPORT_HOME "/opt/local/services/peppol2.0/mq-to-file"
ENV TRANSPORT_CONFIG "/etc/peppol2.0/mq-to-file"

RUN \
    mkdir -p "${TRANSPORT_HOME}" && \
    mkdir -p "${TRANSPORT_CONFIG}" && \
    ln -s "${TRANSPORT_CONFIG}/" "${TRANSPORT_HOME}/config"

WORKDIR "${TRANSPORT_HOME}"
ADD ["build/libs/mq-to-file.jar", "mq-to-file.jar"]

CMD java $JAVA_OPTS -jar mq-to-file.jar

