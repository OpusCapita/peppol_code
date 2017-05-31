FROM d-l-tools.ocnet.local:443/openjdk:8-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

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
ENV CONFIG_SERVER_URI http://configuration-server:8888

WORKDIR "${TRANSPORT_HOME}"
ADD ["build/libs/mq-to-file.jar", "mq-to-file.jar"]
ADD ["bootstrap.yml", "config/bootstrap.yml"]

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["-jar", "mq-to-file.jar"]
