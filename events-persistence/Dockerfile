FROM d-l-tools.ocnet.local:443/openjdk:8-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV EVENTS_PERSISTENCE_HOME /opt/local/services/peppol2.0/events-persistence
ENV EVENTS_PERSISTENCE_CONFIG /etc/peppol2.0/events-persistence
VOLUME "/etc/peppol2.0/events-persistence"

RUN \
    mkdir -p "${EVENTS_PERSISTENCE_HOME}" && \
    mkdir -p "${EVENTS_PERSISTENCE_CONFIG}" && \
    ln -s "${EVENTS_PERSISTENCE_CONFIG}" "${EVENTS_PERSISTENCE_HOME}/config"

WORKDIR "${EVENTS_PERSISTENCE_HOME}"
ADD ["build/libs/events-persistence.jar", "events-persistence.jar"]

CMD java $JAVA_OPTS -jar events-persistence.jar
