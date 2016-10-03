FROM d-l-tools.ocnet.local:443/peppol2.0/tomcat:8
VOLUME /Tmp
ENV SUPPORT_UI_HOME /opt/local/services/peppol2.0/support-ui
ENV PEPPOL_CONFIG_PATH /etc/peppol2.0
RUN mkdir -p "${PEPPOL_CONFIG_PATH}"

VOLUME /etc/peppol2.0

ENV JAVA_OPTS "\
    -Dhttp.proxyHost=10.202.9.33 \
    -Dhttp.proxyPort=8080 \
    -Dhttps.proxyHost=10.202.9.33 \
    -Dhttps.proxyPort=8080 \
    -Dhttp.nonProxyHosts=\"*.local|10.164.1.146\" \
	-Dums.server.url=https://ums.itella.com//itella-ums-server/services/BasicUms "

WORKDIR "${CATALINA_HOME}"
ADD ["build/libs/support-ui.war", "webapps/ROOT.war"]

CMD ["bin/catalina.sh", "run"]

EXPOSE 8080
