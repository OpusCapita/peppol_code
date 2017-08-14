FROM d-l-tools.ocnet.local:443/openjdk:8u141-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV SPRING_APPLICATION_NAME file-to-mq
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

WORKDIR "${TRANSPORT_HOME}"
ADD ["build/libs/file-to-mq.jar", "file-to-mq.jar"]

CMD ["-jar", "file-to-mq.jar"]
