FROM java:8-jre
VOLUME /tmp
ENV VALIDATOR_HOME /opt/local/services/peppol2.0/validator
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

RUN mkdir -p "${VALIDATOR_HOME}"
ADD ["build/libs/validator-0.0.1-SNAPSHOT.jar", "${VALIDATOR_HOME}/validator.jar"]
RUN sh -c 'touch ${VALIDATOR_HOME}/events-persistence.jar'

# enable configuration
RUN mkdir -p "/etc/peppol2.0/validator"
RUN ln -s "/etc/peppol2.0/validator" "${VALIDATOR_HOME}/config"

WORKDIR "${VALIDATOR_HOME}"
ENTRYPOINT ["sh", "-c", "sleep 15 && java -Djava.security.egd=file:/dev/./urandom -Dhttp.proxyHost=10.202.9.33 -Dhttp.proxyPort=8080 -Dhttp.nonProxyHosts=\"10.10.104.128|*.local|itella.net|nocontrol.itella.net|localhost\" -jar ${VALIDATOR_HOME}/validator.jar"]

VOLUME "/etc/peppol2.0"

EXPOSE 8080
EXPOSE 8443

