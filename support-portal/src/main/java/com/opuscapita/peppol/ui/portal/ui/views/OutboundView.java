package com.opuscapita.peppol.ui.portal.ui.views;

import com.opuscapita.peppol.ui.portal.ui.views.fragments.GridFragmentMode;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.GridFragmentType;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.messages.MessagesGridFragment;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.messages.MessagesLazyLoadService;
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

@SpringView(name = ViewName.OUTBOUND)
public class OutboundView extends VerticalLayout implements View {
    @Autowired
    HttpSession httpSession;

    @Autowired
    MessagesLazyLoadService messagesLazyLoadService;

    @PostConstruct
    void init() {
        setSizeFull();
        setDefaultComponentAlignment(Alignment.TOP_LEFT);
        setMargin(false);
        ViewUtil.updateSessionViewObject(httpSession, ViewName.OUTBOUND);

        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.setCaption("Outbound");
        tabSheet.addTab(new MessagesGridFragment(GridFragmentType.OUTBOUND, GridFragmentMode.ALL, messagesLazyLoadService), GridFragmentMode.ALL.toString());
        tabSheet.addTab(new MessagesGridFragment(GridFragmentType.OUTBOUND, GridFragmentMode.DELIVERED, messagesLazyLoadService), GridFragmentMode.DELIVERED.toString());
        tabSheet.addTab(new MessagesGridFragment(GridFragmentType.OUTBOUND, GridFragmentMode.REPROCESSING, messagesLazyLoadService), GridFragmentMode.REPROCESSING.toString());
        tabSheet.addTab(new MessagesGridFragment(GridFragmentType.OUTBOUND, GridFragmentMode.FAILED, messagesLazyLoadService), GridFragmentMode.FAILED.toString());
        tabSheet.addTab(new MessagesGridFragment(GridFragmentType.OUTBOUND, GridFragmentMode.REJECTED, messagesLazyLoadService), GridFragmentMode.REJECTED.toString());
        TabSheetSessionUtils.handleTabsInSession(tabSheet, httpSession);
        addComponent(tabSheet);
    }
}
