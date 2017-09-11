package com.opuscapita.peppol.ui.portal.ui.views.util;

import com.opuscapita.peppol.ui.portal.session.SessionParams;
import com.vaadin.server.VaadinService;

import javax.servlet.http.HttpSession;

public class ViewUtil {
    public static void updateSessionViewObject(HttpSession session, String viewName) {
        if(VaadinService.getCurrentRequest().getWrappedSession(true).getAttribute(SessionParams.LAST_VIEW) == null) {
            VaadinService.getCurrentRequest().getWrappedSession(true).setAttribute(SessionParams.LAST_VIEW, viewName);
        }
    }
}
