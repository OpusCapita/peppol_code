FROM d-l-tools.ocnet.local:443/openjdk:8-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV TRANSPORT_HOME "/opt/local/services/peppol/file-to-mq"
ENV TRANSPORT_CONFIG "/etc/peppol/file-to-mq"

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
ADD ["build/libs/file-to-mq.jar", "file-to-mq.jar"]
ADD ["bootstrap.yml", "config/bootstrap.yml"]

CMD ["-jar", "file-to-mq.jar"]
