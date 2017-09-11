FROM d-l-tools.ocnet.local:443/openjdk:8u141-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV INTEGRATION_TESTS_HOME "/opt/local/services/peppol2.0/integration-tests"
ENV INTEGRATION_TESTS_CONFIG "/etc/peppol2.0/integration-tests"
ENV OXALIS_HOME "/peppol/config/oxalis"

RUN \
    mkdir -p "${INTEGRATION_TESTS_HOME}" && \
    mkdir -p "${INTEGRATION_TESTS_CONFIG}" && \
    ln -s "${INTEGRATION_TESTS_CONFIG}/" "${INTEGRATION_TESTS_HOME}/config"

WORKDIR "${INTEGRATION_TESTS_HOME}"
ADD ["build/libs/integration-tests.jar", "integration-tests.jar"]

ADD ["src/main/resources", "/etc/resources"]
# oxalis-standalone for testing
ADD ["bin/oxalis-standalone.jar", "oxalis-standalone.jar"]
ADD ["bin/send-to-peppol.sh", "send-to-peppol.sh"]

RUN set -x && chmod 500 "${INTEGRATION_TESTS_HOME}/send-to-peppol.sh"\
&& chown -R peppol:elmaci "${INTEGRATION_TESTS_HOME}" "${INTEGRATION_TESTS_CONFIG}"

CMD java -jar -Dspring.profiles.active=development integration-tests.jar /etc/resources/configuration-stage.yaml /etc/integration-test.result /etc/resources/html /tmp/
