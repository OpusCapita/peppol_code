FROM d-l-tools.ocnet.local:443/openjdk:8-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

VOLUME "/etc/peppol2.0/service-discovery"
ENV SERVICE_DISCOVERY_HOME "/opt/local/services/peppol2.0/service-discovery"
ENV SERVICE_DISCOVERY_CONFIG "/etc/peppol2.0/service-discovery"

RUN \
    mkdir -p "${SERVICE_DISCOVERY_HOME}" && \
    mkdir -p "${SERVICE_DISCOVERY_CONFIG}" && \
    ln -s "${SERVICE_DISCOVERY_CONFIG}/" "${SERVICE_DISCOVERY_HOME}/config"

WORKDIR "${SERVICE_DISCOVERY_HOME}"
ADD ["build/libs/service-discovery.jar", "service-discovery.jar"]

CMD java $JAVA_OPTS -jar service-discovery.jar
