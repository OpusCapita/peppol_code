FROM d-l-tools.ocnet.local:443/openjdk:8u141-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV SPRING_APPLICATION_NAME mq-to-jms
ENV TRANSPORT_HOME "/opt/local/services/peppol/mq-to-jms"
ENV TRANSPORT_CONFIG "/etc/peppol/mq-to-jms"

USER root
RUN set -x \
    && mkdir -p "${TRANSPORT_HOME}" "${TRANSPORT_CONFIG}" \
    && chown -R peppol "${TRANSPORT_HOME}" "${TRANSPORT_CONFIG}" \
    && ln -s "${TRANSPORT_CONFIG}/" "${TRANSPORT_HOME}/config"

ADD ["docker-entrypoint.sh", "/docker-entrypoint.sh"]
ENTRYPOINT ["/docker-entrypoint.sh"]

USER peppol
WORKDIR "${TRANSPORT_HOME}"
ADD ["build/libs/mq-to-jms.jar", "mq-to-jms.jar"]

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["-jar", "mq-to-jms.jar"]
