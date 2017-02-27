FROM d-l-tools.ocnet.local:443/oxalis:4
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

VOLUME "/etc/peppol2.0/inbound"
ENV INBOUND_HOME "/opt/local/services/peppol2.0/inbound"
ENV INBOUND_CONFIG "/etc/peppol2.0/inbound"

USER root
RUN set -x \
    && mkdir -p "${INBOUND_HOME}" \
    && mkdir -p "${INBOUND_CONFIG}" \
    && ln -s "${INBOUND_CONFIG}/" "${INBOUND_HOME}/config" \
    && ln -s "${INBOUND_CONFIG}/application.properties" "${INBOUND_HOME}/application.properties" \
    && chown -R peppol:elmaci "${INBOUND_HOME}" "${INBOUND_CONFIG}"

USER peppol
WORKDIR "${INBOUND_HOME}"

# oxalis-standalone for testing
ADD ["bin/oxalis-standalone.jar", "oxalis-standalone.jar"]
ADD ["bin/send-to-peppol.sh", "send-to-peppol.sh"]

# custom persistence module
ADD ["build/libs/inbound.jar", "inbound.jar"]

ENV JAVA_OPTS "\
    -Dhttp.proxyHost=10.202.9.33 \
    -Dhttp.proxyPort=8080 \
    -Dhttps.proxyHost=10.202.9.33 \
    -Dhttps.proxyPort=8080 \
    -Dhttp.nonProxyHosts=\"*.local\" "

CMD ["catalina.sh", "run"]

