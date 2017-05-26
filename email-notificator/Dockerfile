FROM d-l-tools.ocnet.local:443/openjdk:8-peppol
LABEL author="Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>"

ENV EMAIL_NOTIFICATOR_HOME "/opt/local/services/peppol/email-notificator"
ENV EMAIL_NOTIFICATOR_CONFIG "/peppol/config/email-notificator"

USER root
RUN set -x \
    && mkdir -p "${EMAIL_NOTIFICATOR_HOME}" "${EMAIL_NOTIFICATOR_CONFIG}" \
    && chown -R peppol "${EMAIL_NOTIFICATOR_HOME}" "${EMAIL_NOTIFICATOR_CONFIG}" \
    && ln -s "${EMAIL_NOTIFICATOR_CONFIG}/" "${EMAIL_NOTIFICATOR_HOME}/config"

ADD ["docker-entrypoint.sh", "/docker-entrypoint.sh"]
ENTRYPOINT ["/docker-entrypoint.sh"]

USER peppol

WORKDIR "${EMAIL_NOTIFICATOR_HOME}"
ADD ["build/libs/email-notificator.jar", "email-notificator.jar"]

CMD ["-jar", "email-notificator.jar"]
