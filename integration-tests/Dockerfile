FROM d-l-tools.ocnet.local:443/openjdk:8-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

VOLUME "/etc/peppol2.0/integration-tests"
ENV INTEGRATION_TESTS_HOME "/opt/local/services/peppol2.0/integration-tests"
ENV INTEGRATION_TESTS_CONFIG "/etc/peppol2.0/integration-tests"

RUN \
    mkdir -p "${INTEGRATION_TESTS_HOME}" && \
    mkdir -p "${INTEGRATION_TESTS_CONFIG}" && \
    ln -s "${INTEGRATION_TESTS_CONFIG}/" "${INTEGRATION_TESTS_HOME}/config"

WORKDIR "${INTEGRATION_TESTS_HOME}"
ADD ["build/libs/integration-tests.jar", "integration-tests.jar"]

CMD java $JAVA_OPTS -jar integration-tests.jar