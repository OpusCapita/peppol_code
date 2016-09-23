FROM d-l-tools.ocnet.local:443/peppol2.0/tomcat
VOLUME /Tmp
ENV SUPPORT_UI_HOME /opt/local/services/peppol2.0/support-ui
#ENV HTTP_PROXY http://10.202.9.33:8080
#ENV HTTPS_PROXY http://10.202.9.33:8080
ADD ["build/libs/support-ui.war", "${CATALINA_HOME}/webapps/ROOT.war"]