FROM d-l-tools.ocnet.local:443/openjdk:8u111-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV SMOKE_TESTS_HOME "/opt/local/services/peppol/smoke-tests"
ENV SMOKE_TESTS_CONFIG "/peppol/config/smoke-tests"

USER root
RUN set -x \
    && mkdir -p "${SMOKE_TESTS_HOME}" "${SMOKE_TESTS_CONFIG}" \
    && chown -R peppol "${SMOKE_TESTS_HOME}" "${SMOKE_TESTS_CONFIG}" \
    && ln -s "${SMOKE_TESTS_CONFIG}/" "${SMOKE_TESTS_HOME}/config"

USER peppol

WORKDIR "${SMOKE_TESTS_HOME}"
ADD ["build/libs/smoke-tests.jar", "smoke-tests.jar"]


CMD java $JAVA_OPTS -jar smoke-tests.jar

