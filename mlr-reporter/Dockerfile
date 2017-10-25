FROM d-l-tools.ocnet.local:443/openjdk:8u141-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV MLR_REPORTER_HOME "/opt/local/services/peppol/mlr-reporter"
ENV MLR_REPORTER_CONFIG "/peppol/config/mlr-reporter"

USER root
RUN set -x \
    && mkdir -p "${MLR_REPORTER_HOME}" "${MLR_REPORTER_CONFIG}" \
    && chown -R peppol "${MLR_REPORTER_HOME}" "${MLR_REPORTER_CONFIG}" \
    && ln -s "${MLR_REPORTER_CONFIG}/" "${MLR_REPORTER_HOME}/config"

ADD ["docker-entrypoint.sh", "/docker-entrypoint.sh"]
ENTRYPOINT ["/docker-entrypoint.sh"]

USER peppol
WORKDIR "${MLR_REPORTER_HOME}"

ADD ["build/libs/mlr-reporter.jar", "mlr-reporter.jar"]

CMD ["-jar", "mlr-reporter.jar"]
