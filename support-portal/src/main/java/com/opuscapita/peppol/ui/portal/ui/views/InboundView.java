package com.opuscapita.peppol.ui.portal.ui.views;

import com.opuscapita.peppol.ui.portal.model.MessagesRepository;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.GridFragmentMode;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.GridFragmentType;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.messages.MessagesGridFragment;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.messages.MessagesLazyLoadService;
import com.opuscapita.peppol.ui.portal.ui.views.util.TabSheetSessionUtils;
import com.opuscapita.peppol.ui.portal.ui.views.util.ViewUtil;
import com.opuscapita.peppol.ui.portal.util.FileService;
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
    @SuppressWarnings("SpringJavaAutowiringInspection")
    HttpSession httpSession;

    @Autowired
    MessagesRepository messagesRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    MessagesLazyLoadService messagesLazyLoadService;

    @PostConstruct
    void init() {
        setResponsive(true);
        setSizeFull();
        setDefaultComponentAlignment(Alignment.TOP_LEFT);
        setMargin(false);
        ViewUtil.updateSessionViewObject(httpSession, ViewName.INBOUND);

        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.setCaption("Inbound");
        tabSheet.addTab(new MessagesGridFragment(GridFragmentType.INBOUND, GridFragmentMode.ALL, messagesLazyLoadService, fileService), GridFragmentMode.ALL.toString());
        tabSheet.addTab(new MessagesGridFragment(GridFragmentType.INBOUND, GridFragmentMode.DELIVERED, messagesLazyLoadService, fileService), GridFragmentMode.DELIVERED.toString());
        tabSheet.addTab(new MessagesGridFragment(GridFragmentType.INBOUND, GridFragmentMode.REPROCESSING, messagesLazyLoadService, fileService), GridFragmentMode.REPROCESSING.toString());
        tabSheet.addTab(new MessagesGridFragment(GridFragmentType.INBOUND, GridFragmentMode.FAILED, messagesLazyLoadService, fileService), GridFragmentMode.FAILED.toString());
        tabSheet.addTab(new MessagesGridFragment(GridFragmentType.INBOUND, GridFragmentMode.REJECTED, messagesLazyLoadService, fileService), GridFragmentMode.REJECTED.toString());
        TabSheetSessionUtils.handleTabsInSession(tabSheet, httpSession);
        addComponent(tabSheet);
    }

}
