FROM d-l-tools.ocnet.local:443/openjdk:8-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV SPRING_APPLICATION_NAME mq-to-file
ENV TRANSPORT_HOME "/opt/local/services/peppol/mq-to-file"
ENV TRANSPORT_CONFIG "/etc/peppol/mq-to-file"

USER root
RUN set -x \
    && mkdir -p "${TRANSPORT_HOME}" "${TRANSPORT_CONFIG}" \
    && chown -R peppol "${TRANSPORT_HOME}" "${TRANSPORT_CONFIG}" \
    && ln -s "${TRANSPORT_CONFIG}/" "${TRANSPORT_HOME}/config"

ADD ["docker-entrypoint.sh", "/docker-entrypoint.sh"]
ENTRYPOINT ["/docker-entrypoint.sh"]

USER peppol
WORKDIR "${TRANSPORT_HOME}"
ADD ["build/libs/mq-to-file.jar", "mq-to-file.jar"]

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["-jar", "mq-to-file.jar"]
