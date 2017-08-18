FROM d-l-tools.ocnet.local:443/openjdk:8u141-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV OUTBOUND_HOME "/opt/local/services/peppol/outbound"
ENV OUTBOUND_CONFIG "/peppol/config/outbound"

USER root
RUN set -x \
    && mkdir -p "${OUTBOUND_HOME}" "${OUTBOUND_CONFIG}" \
    && chown -R peppol "${OUTBOUND_HOME}" "${OUTBOUND_CONFIG}" \
    && ln -s "${OUTBOUND_CONFIG}/" "${OUTBOUND_HOME}/config"

ADD ["docker-entrypoint.sh", "/docker-entrypoint.sh"]
ENTRYPOINT ["/docker-entrypoint.sh"]

USER peppol

WORKDIR "${OUTBOUND_HOME}"
ADD ["build/libs/outbound.jar", "outbound.jar"]

CMD ["-jar", "outbound.jar"]
EXPOSE 5005