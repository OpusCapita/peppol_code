FROM d-l-tools.ocnet.local:443/openjdk:8u111-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV EVENTING_HOME "/opt/local/services/peppol/eventing"
ENV EVENTING_CONFIG "/peppol/config/eventing"

USER root
RUN set -x \
    && mkdir -p "${EVENTING_HOME}" "${EVENTING_CONFIG}" \
    && chown -R peppol "${EVENTING_HOME}" "${EVENTING_CONFIG}" \
    && ln -s "${EVENTING_CONFIG}/" "${EVENTING_HOME}/config"

ADD ["docker-entrypoint.sh", "/docker-entrypoint.sh"]
ENTRYPOINT ["/docker-entrypoint.sh"]

USER peppol
WORKDIR "${EVENTING_HOME}"

ADD ["build/libs/eventing.jar", "eventing.jar"]

CMD ["-jar", "eventing.jar"]
