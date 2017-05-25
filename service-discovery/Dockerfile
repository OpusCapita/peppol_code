FROM d-l-tools.ocnet.local:443/openjdk:8-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV SERVICE_DISCOVERY_HOME "/opt/local/services/peppol/service-discovery"
ENV SERVICE_DISCOVERY_CONFIG "/peppol/config/service-discovery"
VOLUME "/peppol/config/service-discovery"

RUN set -x \
    && mkdir -p "${SERVICE_DISCOVERY_HOME}" \
    && mkdir -p "${SERVICE_DISCOVERY_CONFIG}" \
    && ln -s "${SERVICE_DISCOVERY_CONFIG}/" "${SERVICE_DISCOVERY_HOME}/config"

ADD ["docker-entrypoint.sh", "/docker-entrypoint.sh"]

WORKDIR "${SERVICE_DISCOVERY_HOME}"
ADD ["build/libs/service-discovery.jar", "service-discovery.jar"]

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["-jar", "service-discovery.jar"]

EXPOSE 8761
