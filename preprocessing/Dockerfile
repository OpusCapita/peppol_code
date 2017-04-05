FROM d-l-tools.ocnet.local:443/openjdk:8-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

VOLUME "/etc/peppol2.0/preprocessing"
ENV PREPROCESSING_HOME "/opt/local/services/peppol2.0/preprocessing"
ENV PREPROCESSING_CONFIG "/etc/peppol2.0/preprocessing"

RUN \
    mkdir -p "${PREPROCESSING_HOME}" && \
    mkdir -p "${PREPROCESSING_CONFIG}" && \
    ln -s "${PREPROCESSING_CONFIG}/" "${PREPROCESSING_HOME}/config"

WORKDIR "${PREPROCESSING_HOME}"
ADD ["build/libs/preprocessing.jar", "preprocessing.jar"]

WORKDIR "${PREPROCESSING_CONFIG}"
ADD ["src/main/resources/document_types", "document_types"]

CMD java $JAVA_OPTS -jar preprocessing.jar

