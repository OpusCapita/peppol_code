FROM d-l-tools.ocnet.local:443/openjdk:8u111-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV PREPROCESSING_HOME "/opt/local/services/peppol/preprocessing"
ENV PREPROCESSING_CONFIG "/peppol/config/preprocessing"

USER root
RUN set -x\
    && mkdir -p "${PREPROCESSING_HOME}" "${PREPROCESSING_CONFIG}" \
    && chown -R peppol "${PREPROCESSING_HOME}" "${PREPROCESSING_CONFIG}" \
    && ln -s "${PREPROCESSING_CONFIG}/" "${PREPROCESSING_HOME}/config"

ADD ["docker-entrypoint.sh", "/docker-entrypoint.sh"]
ENTRYPOINT ["/docker-entrypoint.sh"]

USER peppol

WORKDIR "${PREPROCESSING_HOME}"
ADD ["build/libs/preprocessing.jar", "preprocessing.jar"]

CMD ["-jar", "preprocessing.jar"]
