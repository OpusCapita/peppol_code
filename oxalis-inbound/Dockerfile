FROM d-l-tools.ocnet.local:443/peppol2.0/tomcat:8
MAINTAINER Edgars.Didrihsons@opuscapita.com

ENV OXALIS_HOME "/etc/peppol2.0/oxalis"
ENV OXALIS_LIBS "${CATALINA_HOME}/webapps/oxalis/WEB-INF/libs"

RUN \
    mkdir -p "${OXALIS_HOME}" && \
    mkdir -p "${OXALIS_LIBS}"

ADD ["libs", "${OXALIS_LIBS}"]
ADD ["ee/oxalis.war", "${CATALINA_HOME}/webapps/oxalis.war"]


VOLUME "/etc/peppol2.0"
