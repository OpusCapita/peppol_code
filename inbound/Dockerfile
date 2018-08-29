FROM d-l-tools.ocnet.local:443/openjdk:8u141-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV INBOUND_HOME "/opt/local/services/peppol/inbound"
ENV INBOUND_CONFIG "/peppol/config/inbound"
ENV OXALIS_HOME "/peppol/config/oxalis"

USER root
RUN set -x \
    && mkdir -p "${INBOUND_HOME}" "${INBOUND_CONFIG}" \
    && ln -s "${INBOUND_CONFIG}/" "${INBOUND_HOME}/config" 
    

ADD ["docker-entrypoint.sh", "/docker-entrypoint.sh"]
ENTRYPOINT ["/docker-entrypoint.sh"]

WORKDIR "${INBOUND_HOME}"

# oxalis-standalone for testing
ADD ["bin/oxalis-standalone.jar", "oxalis-standalone.jar"]
ADD ["bin/send-to-peppol.sh", "send-to-peppol.sh"]

# custom persistence module
ADD ["build/libs/inbound.jar", "inbound.jar"]

# set the correct user, group and permissions
RUN set -x \
    && chown -R peppol:elmaci "${INBOUND_HOME}" "${INBOUND_CONFIG}" \
    && chmod 500 "${INBOUND_HOME}/send-to-peppol.sh"

USER peppol

CMD ["-jar", "inbound.jar"]
EXPOSE 8080

