FROM d-l-tools.ocnet.local:443/openjdk:8u141-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV ZUUL_PROXY_HOME "/opt/local/services/peppol/zuul-proxy"
ENV ZUUL_PROXY_CONFIG "/peppol/config/zuul-proxy"

USER root
RUN set -x \
    && mkdir -p "${ZUUL_PROXY_HOME}" "${ZUUL_PROXY_CONFIG}" \
    && chown -R peppol "${ZUUL_PROXY_HOME}" "${ZUUL_PROXY_CONFIG}" \
    && ln -s "${ZUUL_PROXY_CONFIG}/" "${ZUUL_PROXY_HOME}/config"

ADD ["docker-entrypoint.sh", "/docker-entrypoint.sh"]
ENTRYPOINT ["/docker-entrypoint.sh"]

USER peppol

WORKDIR "${ZUUL_PROXY_HOME}"
ADD ["build/libs/zuul-proxy.jar", "zuul-proxy.jar"]

CMD ["-jar", "zuul-proxy.jar"]

EXPOSE 8989 8769
