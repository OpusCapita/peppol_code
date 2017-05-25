FROM d-l-tools.ocnet.local:443/openjdk:8-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV ZUUL_PROXY_HOME "/opt/local/services/peppol/zuul-proxy"
ENV ZUUL_PROXY_CONFIG "/peppol/config/zuul-proxy"

RUN set -x \
    && mkdir -p "${ZUUL_PROXY_HOME}" \
    && mkdir -p "${ZUUL_PROXY_CONFIG}" \
    && ln -s "${ZUUL_PROXY_CONFIG}/" "${ZUUL_PROXY_HOME}/config"

ADD ["docker-entrypoint.sh", "/docker-entrypoint.sh"]
ENTRYPOINT ["/docker-entrypoint.sh"]

WORKDIR "${ZUUL_PROXY_HOME}"
ADD ["build/libs/zuul-proxy.jar", "zuul-proxy.jar"]

CMD ["-jar", "zuul-proxy.jar"]

EXPOSE 8989
