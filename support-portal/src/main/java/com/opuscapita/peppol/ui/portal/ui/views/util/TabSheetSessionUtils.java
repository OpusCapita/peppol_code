package com.opuscapita.peppol.ui.portal.ui.views.util;

import com.opuscapita.peppol.ui.portal.session.SessionParams;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.GridFragment;
import com.vaadin.ui.TabSheet;

import javax.servlet.http.HttpSession;

public class TabSheetSessionUtils {

    public static void handleTabsInSession(TabSheet tabSheet, HttpSession session) {
        System.out.println(session);
        Object lastView = session.getAttribute(SessionParams.LAST_VIEW);
        System.out.println("Last view: " + lastView);
        if (lastView != null) {
            Object lastTab = getLastTabSessionObject(session, lastView);
            System.out.println("Last tab: " + lastTab);
            if (lastTab != null) {
                tabSheet.iterator().forEachRemaining(tab -> {
                    if (((GridFragment) tab).getTag().equals(lastTab.toString())) {
                        tabSheet.setSelectedTab(tab);
                    }
                });
            }
        }
        tabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(TabSheet.SelectedTabChangeEvent event) {
                session.setAttribute(lastView + "_" + SessionParams.LAST_TAB, ((GridFragment) event.getTabSheet().getSelectedTab()).getTag());
                System.out.println("Changing tab to: " + getLastTabSessionObject(session, lastView));
            }
        });
    }

    private static Object getLastTabSessionObject(HttpSession httpSession, Object lastView) {
        System.out.println("Last tab name: " + lastView + "_" + SessionParams.LAST_TAB);
        return httpSession.getAttribute(lastView + "_" + SessionParams.LAST_TAB);
    }
}
