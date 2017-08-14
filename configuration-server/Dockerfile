FROM d-l-tools.ocnet.local:443/openjdk:8u111-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV CONFIG_SERVER_HOME /opt/local/services/peppol/configuration-server
ENV CONFIG_SERVER_CONFIG /peppol/config/configuration-server

USER root
RUN set -x \
    && mkdir -p "${CONFIG_SERVER_HOME}" "${CONFIG_SERVER_CONFIG}" \
    && chown -R peppol "${CONFIG_SERVER_HOME}" "${CONFIG_SERVER_CONFIG}" \
    && ln -s "${CONFIG_SERVER_CONFIG}" "${CONFIG_SERVER_HOME}/config"

ADD ["docker-entrypoint.sh", "/docker-entrypoint.sh"]
ENTRYPOINT ["/docker-entrypoint.sh"]

USER peppol

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
