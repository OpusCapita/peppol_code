FROM d-l-tools.ocnet.local:443/peppol2.0/tomcat:8
MAINTAINER Edgars.Didrihsons@opuscapita.com

ENV OXALIS_LIBS "${CATALINA_HOME}/webapps/oxalis/WEB-INF/libs"
RUN mkdir -p "${OXALIS_LIBS}"

ADD ["libs", "${OXALIS_LIBS}"]
ADD ["ee/oxalis.war", "${CATALINA_HOME}/webapps/oxalis.war"]

