FROM d-l-tools.ocnet.local:443/openjdk:8u141-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV EVENTS_PERSISTENCE_HOME /opt/local/services/peppol/events-persistence
ENV EVENTS_PERSISTENCE_CONFIG /peppol/config/events-persistence

USER root
RUN set -x \
    && mkdir -p "${EVENTS_PERSISTENCE_HOME}" "${EVENTS_PERSISTENCE_CONFIG}" \
    && chown -R peppol "${EVENTS_PERSISTENCE_HOME}" "${EVENTS_PERSISTENCE_CONFIG}" \
    && ln -s "${EVENTS_PERSISTENCE_CONFIG}" "${EVENTS_PERSISTENCE_HOME}/config"

ADD ["docker-entrypoint.sh", "/docker-entrypoint.sh"]
ENTRYPOINT ["/docker-entrypoint.sh"]

USER peppol

WORKDIR "${EVENTS_PERSISTENCE_HOME}"
ADD ["build/libs/events-persistence.jar", "events-persistence.jar"]

CMD ["-jar", "events-persistence.jar"]
