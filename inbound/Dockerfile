FROM d-l-tools.ocnet.local:443/peppol2.0/tomcat:8
MAINTAINER Edgars.Didrihsons@opuscapita.com

ENV OXALIS_HOME "/etc/peppol2.0/oxalis"
RUN mkdir -p "${OXALIS_HOME}"

ADD ["ee/oxalis.war", "${CATALINA_HOME}/webapps/peppol-ap-inbound.war"]

VOLUME "/etc/peppol2.0"
