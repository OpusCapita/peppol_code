FROM d-l-tools.ocnet.local:443/openjdk:8-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV CONFIG_SERVER_HOME /opt/local/services/peppol/configuration-server
ENV CONFIG_SERVER_CONFIG /peppol/config/configuration-server
RUN set -x \
    && mkdir -p "${CONFIG_SERVER_HOME}" \
    && mkdir -p /var/local/services/peppol2.0/configuration-server/checkout \
    && mkdir -p "${CONFIG_SERVER_CONFIG}" \
    && ln -s "${CONFIG_SERVER_CONFIG}" "${CONFIG_SERVER_HOME}/config"

ADD ["docker-entrypoint.sh", "/docker-entrypoint.sh"]
ENTRYPOINT ["/docker-entrypoint.sh"]

ENV JAVA_OPTS "\
    -Dhttp.proxyHost=10.202.9.33 \
    -Dhttp.proxyPort=8080 \
    -Dhttps.proxyHost=10.202.9.33 \
    -Dhttps.proxyPort=8080 \
    -Dhttp.nonProxyHosts=\"*.local|nocontrol.itella.net\" "

WORKDIR "${CONFIG_SERVER_HOME}"
ADD ["build/libs/configuration-server.jar", "configuration-server.jar"]

CMD ["-jar", "configuration-server.jar"]

EXPOSE 8888
