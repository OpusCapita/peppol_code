FROM java:8-jre
VOLUME /tmp
ENV CONFIG_SERVER_HOME /opt/local/services/peppol2.0/config-server
ENV http_proxy http://10.202.9.33:8080
ENV https_proxy http://10.202.9.33:8080
ENV no_proxy "10.10.104.128,localhost,.local,nocontrol.itella.net,group.itella.net"
ENV HTTP_PROXY http://10.202.9.33:8080
ENV HTTPS_PROXY http://10.202.9.33:8080
ENV NO_PROXY "10.10.104.128,.local,nocontrol.itella.net,group.itella.net"
ENV http.proxyHost 10.202.9.33
ENV http.proxyPort 8080
ENV https.proxyHost 10.202.9.33
ENV https.proxyPort 8080
ENV http.nonProxyHosts "10.10.104.128|.local,nocontrol|itella.net|group.itella.net|localhost|127.0.0.1"
RUN mkdir -p "${CONFIG_SERVER_HOME}"
RUN mkdir -p /var/local/services/peppol2.0/configuration-server/checkout
ADD ["build/libs/configuration-server-0.0.1-SNAPSHOT.jar", "${CONFIG_SERVER_HOME}/config-server.jar"]
RUN sh -c 'touch ${CONFIG_SERVER_HOME}/config-server.jar'

# enable configuration
RUN mkdir -p "/etc/peppol2.0/configuration-server"
RUN ln -s "/etc/peppol2.0/configuration-server/bootstrap.yml" "${CONFIG_SERVER_HOME}/bootstrap.yml"

WORKDIR "${CONFIG_SERVER_HOME}"
ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -Dhttp.proxyHost=10.202.9.33 -Dhttp.proxyPort=8080 -Dhttp.nonProxyHosts=\"10.10.104.128|*.local|localhost\" -jar ${CONFIG_SERVER_HOME}/config-server.jar"]
#ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar ${CONFIG_SERVER_HOME}/config-server.jar"]

VOLUME "/etc/peppol2.0"

EXPOSE 8888
EXPOSE 80
EXPOSE 443

