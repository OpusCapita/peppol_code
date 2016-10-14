FROM d-l-tools.ocnet.local:443/peppol2.0/lib/oxalis:4
MAINTAINER Edgars.Didrihsons@opuscapita.com

VOLUME "/etc/peppol2.0"
ENV PREPROCESSING_HOME "/opt/local/services/peppol2.0/preprocessing"
ENV PREPROCESSING_CONFIG "/etc/peppol2.0/preprocessing"

RUN \
    mkdir -p "${PREPROCESSING_HOME}" && \
    mkdir -p "${PREPROCESSING_CONFIG}" && \
    ln -s "${PREPROCESSING_CONFIG}/" "${PREPROCESSING_HOME}/config"

ENV JAVA_OPTS "\
    -Dhttp.proxyHost=10.202.9.33 \
    -Dhttp.proxyPort=8080 \
    -Dhttps.proxyHost=10.202.9.33 \
    -Dhttps.proxyPort=8080 \
    -Dhttp.nonProxyHosts=\"*.local\" "

WORKDIR "${PREPROCESSING_HOME}"

# oxalis-standalone for testing
ADD ["bin/oxalis-standalone.jar", "oxalis-standalone.jar"]
ADD ["bin/send-to-peppol.sh", "send-to-peppol.sh"]

# custom persistence module
ADD ["build/libs/preprocessing.jar", "preprocessing.jar"]

CMD ["catalina.sh", "run"]

