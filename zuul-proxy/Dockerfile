FROM d-l-tools.ocnet.local:443/openjdk:8-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

VOLUME "/etc/peppol2.0/zuul-proxy"
ENV ZUUL_PROXY_HOME "/opt/local/services/peppol2.0/zuul-proxy"
ENV ZUUL_PROXY_CONFIG "/etc/peppol2.0/zuul-proxy"

RUN \
    mkdir -p "${ZUUL_PROXY_HOME}" && \
    mkdir -p "${ZUUL_PROXY_CONFIG}" && \
    ln -s "${ZUUL_PROXY_CONFIG}/" "${ZUUL_PROXY_HOME}/config"

WORKDIR "${ZUUL_PROXY_HOME}"
ADD ["build/libs/zuul-proxy.jar", "zuul-proxy.jar"]

CMD java $JAVA_OPTS -jar zuul-proxy.jar
