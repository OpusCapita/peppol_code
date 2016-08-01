FROM java:8-jre
VOLUME /tmp
ENV EVENTS_PERSISTENCE_HOME /opt/local/services/peppol2.0/events-persistence
ENV http_proxy http://10.202.9.33:8080
ENV https_proxy http://10.202.9.33:8080
ENV no_proxy "10.10.104.128,.local,nocontrol.itella.net,group.itella.net"
ENV HTTP_PROXY http://10.202.9.33:8080
ENV HTTPS_PROXY http://10.202.9.33:8080
ENV NO_PROXY "10.10.104.128,.local,nocontrol.itella.net,group.itella.net"
ENV http.proxyHost 10.202.9.33
ENV http.proxyPort 8080
ENV https.proxyHost 10.202.9.33
ENV https.proxyPort 8080
ENV http.nonProxyHosts "10.10.104.128|.local,nocontrol|itella.net|group.itella.net|localhost|127.0.0.1"
RUN mkdir -p "${EVENTS_PERSISTENCE_HOME}"
ADD ["build/libs/events-persistence-0.0.1-SNAPSHOT.jar", "${EVENTS_PERSISTENCE_HOME}/events-persistence.jar"]
RUN sh -c 'touch ${EVENTS_PERSISTENCE_HOME}/events-persistence.jar'
ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -Dhttp.proxyHost=10.202.9.33 -Dhttp.proxyPort=8080 -Dhttp.nonProxyHosts=\"10.10.104.128|.local|itella.net|nocontrol.itella.net|localhost\" -jar ${EVENTS_PERSISTENCE_HOME}/events-persistence.jar"]
EXPOSE 8080
EXPOSE 8443