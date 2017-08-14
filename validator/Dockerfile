FROM d-l-tools.ocnet.local:443/openjdk:8u111-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV VALIDATOR_HOME "/opt/local/services/peppol/validator"
ENV VALIDATOR_CONFIG "/peppol/config/validator"

USER root
RUN set -x \
    && mkdir -p "${VALIDATOR_HOME}" "${VALIDATOR_CONFIG}" \
    && chown -R peppol "${VALIDATOR_HOME}" "${VALIDATOR_CONFIG}" \
    && ln -s "${VALIDATOR_CONFIG}/" "${VALIDATOR_HOME}/config"

ADD ["docker-entrypoint.sh", "/docker-entrypoint.sh"]
ENTRYPOINT ["/docker-entrypoint.sh"]

USER peppol

WORKDIR "${VALIDATOR_HOME}"
ADD ["build/libs/validator.jar", "validator.jar"]

CMD ["-jar", "validator.jar"]
EXPOSE 8080
