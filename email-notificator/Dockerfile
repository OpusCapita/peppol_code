FROM d-l-tools.ocnet.local:443/openjdk:8-peppol
MAINTAINER Edgars Didrihsons <Edgars.Didrihsons@opuscapita.com>

VOLUME "/etc/peppol2.0/email-notificator"
ENV EMAIL_NOTIFICATOR_HOME "/opt/local/services/peppol2.0/email-notificator"
ENV EMAIL_NOTIFICATOR_CONFIG "/etc/peppol2.0/email-notificator"

RUN \
    mkdir -p "${EMAIL_NOTIFICATOR_HOME}" && \
    mkdir -p "${EMAIL_NOTIFICATOR_CONFIG}" && \
    ln -s "${EMAIL_NOTIFICATOR_CONFIG}/" "${EMAIL_NOTIFICATOR_HOME}/config"

WORKDIR "${EMAIL_NOTIFICATOR_HOME}"
ADD ["build/libs/email-notificator.jar", "email-notificator.jar"]

CMD ["java", "$JAVA_OPTS", "-jar", "email-notificator.jar"]

