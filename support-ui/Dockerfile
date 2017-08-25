FROM d-l-tools.ocnet.local:443/tomcat:8-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV PEPPOL_CONFIG_PATH "/peppol/config/support-ui"
ENV CATALINA_BASE "/opt/local/services/peppol/support-ui/tomcat"

USER root
RUN set -x \
    && mkdir -p "${PEPPOL_CONFIG_PATH}" \
    && mkdir -p \
        "${PEPPOL_CONFIG_PATH}" \
        "${PEPPOL_CONFIG_PATH}/tomcat/conf" \
        "${PEPPOL_CONFIG_PATH}/tomcat/logs" \
        "${CATALINA_BASE}/bin" \
        "${CATALINA_BASE}/webapps" \
        "${CATALINA_BASE}/work" \
        "${CATALINA_BASE}/temp" \
    && ln -s "${PEPPOL_CONFIG_PATH}/tomcat/conf" "${CATALINA_BASE}/conf" \
    && ln -s "${PEPPOL_CONFIG_PATH}/tomcat/logs" "${CATALINA_BASE}/logs" \
    && cp -r "${CATALINA_HOME}/conf" "${PEPPOL_CONFIG_PATH}/tomcat" \
    && chown -R peppol:elmaci "${PEPPOL_CONFIG_PATH}" "${CATALINA_BASE}"

ADD ["docker-entrypoint.sh", "/docker-entrypoint.sh"]
ENTRYPOINT ["/docker-entrypoint.sh"]

USER peppol

WORKDIR "${CATALINA_BASE}"
ADD ["build/libs/support-ui.war", "webapps/ROOT.war"]

ENV JAVA_OPTS "\
    -Dhttp.proxyHost=10.202.9.33 \
    -Dhttp.proxyPort=8080 \
    -Dhttps.proxyHost=10.202.9.33 \
    -Dhttps.proxyPort=8080 \
    -Dhttp.nonProxyHosts=\"*.local|10.164.1.146\" \
    -Duser.timezone=Europe/Helsinki \
    -Dums.server.url=https://ums.itella.com//itella-ums-server/services/BasicUms"

CMD ["catalina.sh", "run"]

EXPOSE 8080
