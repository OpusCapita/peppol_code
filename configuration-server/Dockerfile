FROM java:8-jre
VOLUME /tmp
ENV CONFIG_SERVER_HOME /opt/local/services/peppol/config-server
RUN mkdir -p "${CONFIG_SERVER_HOME}"
ADD ["build/libs/configuration-server-0.0.1-SNAPSHOT.jar", "${CONFIG_SERVER_HOME}/config-server.jar"]
RUN sh -c 'touch ${CONFIG_SERVER_HOME}/config-server.jar'
ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar ${CONFIG_SERVER_HOME}/config-server.jar"]
EXPOSE 8888