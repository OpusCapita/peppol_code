FROM d-l-tools.ocnet.local:443/openjdk:8u141-peppol
LABEL author="Daniil Kalnin <daniil.kalnin@opuscapita.com>"

ENV SUPPORT_PORTAL_HOME "/opt/local/services/peppol/support-portal"
ENV SUPPORT_PORTAL_CONFIG "/peppol/config/support-portal"

USER root
RUN set -x \
    && mkdir -p "${SUPPORT_PORTAL_HOME}" "${SUPPORT_PORTAL_CONFIG}" \
    && chown -R peppol "${SUPPORT_PORTAL_HOME}" "${SUPPORT_PORTAL_CONFIG}" \
    && ln -s "${SUPPORT_PORTAL_CONFIG}/" "${SUPPORT_PORTAL_HOME}/config"

ADD ["docker-entrypoint.sh", "/docker-entrypoint.sh"]
ENTRYPOINT ["/docker-entrypoint.sh"]

USER peppol

WORKDIR "${SUPPORT_PORTAL_HOME}"
ADD ["build/libs/support-portal.jar", "support-portal.jar"]

CMD ["-jar", "support-portal.jar"]
