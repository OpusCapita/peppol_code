FROM d-l-tools.ocnet.local:443/peppol2.0/oxalis:4
MAINTAINER Edgars.Didrihsons@opuscapita.com

ENV INBOUND_HOME "/opt/local/services/peppol2.0/inbound"
ENV INBOUND_CONFIG "/etc/peppol2.0/inbound"

RUN \
    mkdir -p "${INBOUND_HOME}" && \
    mkdir -p "${INBOUND_CONFIG}" && \
    ln -s "${INBOUND_CONFIG}" "${INBOUND_HOME}/config"

ADD ["build/libs/inbound-0.0.1-SNAPSHOT.jar", "${INBOUND_HOME}/inbound.jar"]

