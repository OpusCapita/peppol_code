FROM d-l-tools.ocnet.local:443/openjdk:8-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

VOLUME "/etc/peppol2.0/outbound"
ENV OUTBOUND_HOME "/opt/local/services/peppol2.0/outbound"
ENV OUTBOUND_CONFIG "/etc/peppol2.0/outbound"

RUN \
    mkdir -p "${OUTBOUND_HOME}" && \
    mkdir -p "${OUTBOUND_CONFIG}" && \
    ln -s "${OUTBOUND_CONFIG}/" "${OUTBOUND_HOME}/config"

WORKDIR "${OUTBOUND_HOME}"
ADD ["build/libs/outbound.jar", "outbound.jar"]

CMD java $JAVA_OPTS -jar outbound.jar

