FROM java:8-jre
VOLUME /tmp
ENV CONFIG_SERVER_HOME /opt/local/services/peppol/config-server
RUN mkdir -p "${CONFIG_SERVER_HOME}"
ADD ["build/libs/configuration-server-0.0.1-SNAPSHOT.jar", "${CONFIG_SERVER_HOME}/config-server.jar"]
RUN sh -c 'touch ${CONFIG_SERVER_HOME}/config-server.jar'
ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -Dhttp.proxyHost=10.202.9.33 -Dhttp.proxyPort=8080 -Dhttp.noProxyHosts=\"10.10.104.128|.local|itella.net\" -jar ${CONFIG_SERVER_HOME}/config-server.jar"]
EXPOSE 8888