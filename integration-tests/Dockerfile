FROM d-l-tools.ocnet.local:443/openjdk:8u141-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV INTEGRATION_TESTS_HOME "/opt/local/services/peppol2.0/integration-tests"
ENV INTEGRATION_TESTS_CONFIG "/peppol/config/integration-tests"
ENV OXALIS_HOME "/peppol/config/oxalis"

USER root

ADD ["src/main/resources", "/etc/resources"]
RUN set -x \
    && mkdir -p "${INTEGRATION_TESTS_HOME}" "${INTEGRATION_TESTS_CONFIG}" \
    && chown -R peppol "${INTEGRATION_TESTS_HOME}" "${INTEGRATION_TESTS_CONFIG}" \
    && chown -R peppol /etc/resources \
    && ln -s "${INTEGRATION_TESTS_CONFIG}/" "${INTEGRATION_TESTS_HOME}/config"

USER peppol

WORKDIR "${INTEGRATION_TESTS_HOME}"
ADD ["build/libs/integration-tests.jar", "integration-tests.jar"]

# oxalis-standalone for testing
ADD ["bin/oxalis-standalone.jar", "oxalis-standalone.jar"]
ADD ["bin/send-to-peppol.sh", "send-to-peppol.sh"]

CMD java -jar -Dspring.profiles.active=development integration-tests.jar /etc/resources/configuration-stage.yaml /etc/integration-test.result /etc/resources/html /tmp/
