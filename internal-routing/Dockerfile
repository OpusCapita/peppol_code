FROM d-l-tools.ocnet.local:443/openjdk:8-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV INTERNAL_ROUTING_HOME "/opt/local/services/peppol/internal-routing"
ENV INTERNAL_ROUTING_CONFIG "/peppol/config/internal-routing"

USER root
RUN set -x \
    && mkdir -p "${INTERNAL_ROUTING_HOME}" "${INTERNAL_ROUTING_CONFIG}" \
    && chown -R peppol "${INTERNAL_ROUTING_HOME}" "${INTERNAL_ROUTING_CONFIG}" \
    && ln -s "${INTERNAL_ROUTING_CONFIG}/" "${INTERNAL_ROUTING_HOME}/config"

ADD ["docker-entrypoint.sh", "/docker-entrypoint.sh"]
ENTRYPOINT ["/docker-entrypoint.sh"]

USER peppol

WORKDIR "${INTERNAL_ROUTING_HOME}"
ADD ["build/libs/internal-routing.jar", "internal-routing.jar"]

CMD ["-jar", "internal-routing.jar"]
