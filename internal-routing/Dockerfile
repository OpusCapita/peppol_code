FROM d-l-tools.ocnet.local:443/openjdk:8-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

VOLUME "/etc/peppol2.0/internal-routing"
ENV INTERNAL_ROUTING_HOME "/opt/local/services/peppol2.0/internal-routing"
ENV INTERNAL_ROUTING_CONFIG "/etc/peppol2.0/internal-routing"

RUN \
    mkdir -p "${INTERNAL_ROUTING_HOME}" && \
    mkdir -p "${INTERNAL_ROUTING_CONFIG}" && \
    ln -s "${INTERNAL_ROUTING_CONFIG}/" "${INTERNAL_ROUTING_HOME}/config"

WORKDIR "${INTERNAL_ROUTING_HOME}"
ADD ["build/libs/internal-routing.jar", "internal-routing.jar"]

CMD java $JAVA_OPTS -jar internal-routing.jar

