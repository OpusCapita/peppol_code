FROM java:8-jre
MAINTAINER Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>

VOLUME "/etc/peppol2.0/integration-tests"
ENV SMOKE_TESTS_HOME "/opt/local/services/peppol2.0/integration-tests"
ENV SMOKE_TESTS_CONFIG "/etc/peppol2.0/integration-tests"

RUN \
    mkdir -p "${SMOKE_TESTS_HOME}" && \
    mkdir -p "${SMOKE_TESTS_CONFIG}" && \
    ln -s "${SMOKE_TESTS_CONFIG}/" "${SMOKE_TESTS_HOME}/config"

WORKDIR "${SMOKE_TESTS_HOME}"
ADD ["build/libs/integration-tests.jar", "integration-tests.jar"]

ENTRYPOINT ["java", "-jar", "integration-testsjar"]