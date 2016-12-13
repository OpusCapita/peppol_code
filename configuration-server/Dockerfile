FROM d-l-tools.ocnet.local:443/openjdk:8-peppol
MAINTAINER Edgars.Didrihsons@opuscapita.com

VOLUME "/etc/peppol2.0/configuration-server"
ENV CONFIG_SERVER_HOME /opt/local/services/peppol2.0/configuration-server
ENV CONFIG_SERVER_CONFIG /etc/peppol2.0/configuration-server
RUN \
    mkdir -p "${CONFIG_SERVER_HOME}" && \
    mkdir -p /var/local/services/peppol2.0/configuration-server/checkout && \
    mkdir -p "${CONFIG_SERVER_CONFIG}" && \
    ln -s "${CONFIG_SERVER_CONFIG}" "${CONFIG_SERVER_HOME}/config"

ENV JAVA_OPTS "\
    -Dhttp.proxyHost=10.202.9.33 \
    -Dhttp.proxyPort=8080 \
    -Dhttps.proxyHost=10.202.9.33 \
    -Dhttps.proxyPort=8080 \
    -Dhttp.nonProxyHosts=\"*.local|nocontrol.itella.net\" "

WORKDIR "${CONFIG_SERVER_HOME}"
ADD ["build/libs/configuration-server.jar", "configuration-server.jar"]

CMD ["java", "-jar", "configuration-server.jar"]

EXPOSE 8888
