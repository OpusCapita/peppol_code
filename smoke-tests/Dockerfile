FROM d-l-tools.ocnet.local:443/openjdk:8-peppol
MAINTAINER Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>

VOLUME "/etc/peppol2.0/smoke-tests"
ENV SMOKE_TESTS_HOME "/opt/local/services/peppol2.0/smoke-tests"
ENV SMOKE_TESTS_CONFIG "/etc/peppol2.0/smoke-tests"

RUN \
    mkdir -p "${SMOKE_TESTS_HOME}" && \
    mkdir -p "${SMOKE_TESTS_CONFIG}" && \
    ln -s "${SMOKE_TESTS_CONFIG}/" "${SMOKE_TESTS_HOME}/config"

WORKDIR "${SMOKE_TESTS_HOME}"
ADD ["build/libs/smoke-tests.jar", "smoke-tests.jar"]

ENTRYPOINT ["java", "$JAVA_OPTS", "-jar", "smoke-tests.jar"]

