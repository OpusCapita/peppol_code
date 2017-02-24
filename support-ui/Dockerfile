FROM d-l-tools.ocnet.local:443/tomcat:8-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV PEPPOL_CONFIG_PATH /etc/peppol2.0/support-ui
VOLUME ${PEPPOL_CONFIG_PATH}

USER root
RUN set -x \
    && mkdir -p "${PEPPOL_CONFIG_PATH}" \
    && chown -R peppol:elmaci "${PEPPOL_CONFIG_PATH}"

USER peppol

WORKDIR "${CATALINA_HOME}"
ADD ["build/libs/support-ui.war", "webapps/ROOT.war"]

ENV JAVA_OPTS "\
    -Dhttp.proxyHost=10.202.9.33 \
    -Dhttp.proxyPort=8080 \
    -Dhttps.proxyHost=10.202.9.33 \
    -Dhttps.proxyPort=8080 \
    -Dhttp.nonProxyHosts=\"*.local|10.164.1.146\" \
    -Dums.server.url=https://ums.itella.com//itella-ums-server/services/BasicUms"

CMD ["bin/catalina.sh", "run"]

EXPOSE 8080
