FROM d-l-tools.ocnet.local:443/openjdk:8-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

VOLUME "/etc/peppol2.0/file-to-mq"
ENV TRANSPORT_HOME "/opt/local/services/peppol2.0/file-to-mq"
ENV TRANSPORT_CONFIG "/etc/peppol2.0/file-to-mq"

USER root
RUN set -x \
    && mkdir -p "${TRANSPORT_HOME}" "${TRANSPORT_CONFIG}" \
    && chown -R peppol "${TRANSPORT_HOME}" "${TRANSPORT_CONFIG}" \
    && ln -s "${TRANSPORT_CONFIG}/" "${TRANSPORT_HOME}/config"

USER peppol

WORKDIR "${TRANSPORT_HOME}"
ADD ["build/libs/file-to-mq.jar", "file-to-mq.jar"]

CMD java $JAVA_OPTS -jar file-to-mq.jar

