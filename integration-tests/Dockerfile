FROM d-l-tools.ocnet.local:443/openjdk:8-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

VOLUME "/etc"
VOLUME "/etc/tmp"
VOLUME "/etc/peppol2.0/integration-tests"
VOLUME "/etc/resources"
VOLUME "/etc/resources/validation-result"

ENV INTEGRATION_TESTS_HOME "/opt/local/services/peppol2.0/integration-tests"
ENV INTEGRATION_TESTS_CONFIG "/etc/peppol2.0/integration-tests"

RUN \
    mkdir -p "${INTEGRATION_TESTS_HOME}" && \
    mkdir -p "${INTEGRATION_TESTS_CONFIG}" && \
    ln -s "${INTEGRATION_TESTS_CONFIG}/" "${INTEGRATION_TESTS_HOME}/config"

WORKDIR "${INTEGRATION_TESTS_HOME}"
ADD ["build/libs/integration-tests.jar", "integration-tests.jar"]

CMD java -jar integration-tests.jar  /etc/resources/configuration-stage.yaml /etc/integration-test.result /etc/resources/html/summary.ftl /etc/tmp/