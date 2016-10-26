FROM d-l-tools.ocnet.local:443/peppol2.0/lib/oxalis:4
MAINTAINER Edgars.Didrihsons@opuscapita.com

VOLUME "/etc/peppol2.0"
ENV INBOUND_HOME "/opt/local/services/peppol2.0/inbound"
ENV INBOUND_CONFIG "/etc/peppol2.0/inbound"

RUN \
    mkdir -p "${INBOUND_HOME}" && \
    mkdir -p "${INBOUND_CONFIG}" && \
    ln -s "${INBOUND_CONFIG}/" "${INBOUND_HOME}/config"

ENV JAVA_OPTS "\
    -Dhttp.proxyHost=10.202.9.33 \
    -Dhttp.proxyPort=8080 \
    -Dhttps.proxyHost=10.202.9.33 \
    -Dhttps.proxyPort=8080 \
    -Dhttp.nonProxyHosts=\"*.local\" "

WORKDIR "${INBOUND_HOME}"

# oxalis-standalone for testing
ADD ["bin/oxalis-standalone.jar", "oxalis-standalone.jar"]
ADD ["bin/send-to-peppol.sh", "send-to-peppol.sh"]

# custom persistence module
ADD ["build/libs/inbound.jar", "inbound.jar"]

CMD ["catalina.sh", "run"]

