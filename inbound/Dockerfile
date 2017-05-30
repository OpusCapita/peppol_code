FROM d-l-tools.ocnet.local:443/oxalis:4.0.0-RC1
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV INBOUND_HOME "/opt/local/services/peppol/inbound"
ENV INBOUND_CONFIG "/peppol/config/inbound"

USER root
RUN set -x \
    && mkdir -p "${INBOUND_HOME}" "${INBOUND_CONFIG}" \
    && ln -s "${INBOUND_CONFIG}/" "${INBOUND_HOME}/config" \
    && ln -s "${INBOUND_CONFIG}/application.properties" "${INBOUND_HOME}/application.properties"

ADD ["docker-entrypoint.sh", "/docker-entrypoint.sh"]
ENTRYPOINT ["/docker-entrypoint.sh"]

WORKDIR "${INBOUND_HOME}"

# oxalis-standalone for testing
ADD ["bin/oxalis-standalone.jar", "oxalis-standalone.jar"]
ADD ["bin/send-to-peppol.sh", "send-to-peppol.sh"]

# custom persistence module
ADD ["build/libs/inbound.jar", "inbound.jar"]

# set the correct user, group and permissions
RUN set -x \
    && chown -R peppol:elmaci "${INBOUND_HOME}" "${INBOUND_CONFIG}" \
    && chmod 500 "${INBOUND_HOME}/send-to-peppol.sh"

USER peppol
ENV JAVA_OPTS "\
    -Dhttp.proxyHost=10.202.9.33 \
    -Dhttp.proxyPort=8080 \
    -Dhttps.proxyHost=10.202.9.33 \
    -Dhttps.proxyPort=8080 \
    -Dhttp.nonProxyHosts=\"*.local\" "

CMD ["catalina.sh", "run"]

