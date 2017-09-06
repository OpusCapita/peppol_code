package com.opuscapita.peppol.ui.portal.ui.views;

import com.opuscapita.peppol.ui.portal.ui.views.fragments.GridFragment;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.GridFragmentMode;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.GridFragmentType;
import com.opuscapita.peppol.ui.portal.ui.views.util.TabSheetSessionUtils;
import com.opuscapita.peppol.ui.portal.ui.views.util.ViewUtil;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

@SpringView(name = ViewName.INBOUND)
public class InboundView extends VerticalLayout implements View {
    @Autowired
    HttpSession httpSession;

    @PostConstruct
    void init() {
        setSizeFull();
        setDefaultComponentAlignment(Alignment.TOP_LEFT);
        setMargin(false);
        ViewUtil.updateSessionViewObject(httpSession, ViewName.INBOUND);

        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.setCaption("Inbound");
        tabSheet.addTab(new GridFragment(GridFragmentType.INBOUND, GridFragmentMode.ALL), GridFragmentMode.ALL.toString());
        tabSheet.addTab(new GridFragment(GridFragmentType.INBOUND, GridFragmentMode.DELIVERED), GridFragmentMode.DELIVERED.toString());
        tabSheet.addTab(new GridFragment(GridFragmentType.INBOUND, GridFragmentMode.REPROCESSING), GridFragmentMode.REPROCESSING.toString());
        tabSheet.addTab(new GridFragment(GridFragmentType.INBOUND, GridFragmentMode.FAILED), GridFragmentMode.FAILED.toString());
        tabSheet.addTab(new GridFragment(GridFragmentType.INBOUND, GridFragmentMode.REJECTED), GridFragmentMode.REJECTED.toString());
        TabSheetSessionUtils.handleTabsInSession(tabSheet, httpSession);
        addComponent(tabSheet);
    }


}
