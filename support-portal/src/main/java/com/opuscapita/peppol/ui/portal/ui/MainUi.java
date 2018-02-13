package com.opuscapita.peppol.ui.portal.ui;

import com.opuscapita.peppol.ui.portal.session.SessionParams;
import com.opuscapita.peppol.ui.portal.ui.views.ViewName;
import com.vaadin.annotations.Theme;
import com.vaadin.event.LayoutEvents;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpSession;

@SpringUI
@Theme("peppol")
public class MainUi extends UI {
    public static final int NAVIGATION_BUTTON_WIDTH = 140;
    @Value(value = "${peppol.portal.baseUrl:/portal}")
    String baseUrl;

    Navigator navigator;

    @Autowired
    HttpSession httpSession;

    @Autowired
    SpringViewProvider viewProvider;

    public MainUi() {

    }

    @Override
    protected void init(VaadinRequest request) {
        setResponsive(true);
        setMobileHtml5DndEnabled(true);

        VerticalLayout screen = new VerticalLayout();
        screen.setSizeFull();
        screen.setMargin(false);
        screen.setDefaultComponentAlignment(Alignment.TOP_LEFT);

        HorizontalLayout header = initHeader();

        HorizontalLayout body = initBody();

        VerticalLayout leftMenu = initLeftMenu();

        HorizontalLayout footer = initFooter();

        populateLeftMenu(leftMenu);

        VerticalLayout contentPanel = initContentPanel();

        body.addComponent(leftMenu);
        body.setExpandRatio(leftMenu, 1);
        body.addComponent(contentPanel);
        body.setExpandRatio(contentPanel, 6);

        screen.addComponent(header);
        screen.addComponent(body);
        screen.addComponent(footer);
        screen.setExpandRatio(body, 1);
        screen.setDefaultComponentAlignment(Alignment.TOP_CENTER);

        setContent(screen);

        initNavigator(contentPanel);
    }

    private HorizontalLayout initFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidth(100, Unit.PERCENTAGE);
        footer.setHeightUndefined();
        footer.setMargin(new MarginInfo(true, false, false, false));

        Resource res = new ClassResource("/img/bg-footer.png");
        Image image = new Image(null, res);
        footer.addComponent(image);
        return footer;
    }

    private VerticalLayout initContentPanel() {
        VerticalLayout contentPanel = new VerticalLayout();
        contentPanel.setSizeFull();
        contentPanel.setMargin(new MarginInfo(false, true, true, false));
        contentPanel.setDefaultComponentAlignment(Alignment.TOP_LEFT);
        return contentPanel;
    }

    private void populateLeftMenu(VerticalLayout leftMenu) {
        Button inboundBtn = new Button("Inbound");
        inboundBtn.setWidth(NAVIGATION_BUTTON_WIDTH, Unit.PIXELS);
        inboundBtn.addStyleName("cellFontSizeOverride");
        inboundBtn.addClickListener(createNavigatorClickListener(ViewName.INBOUND));
        Button outboundBtn = new Button("Outbound");
        outboundBtn.setWidth(NAVIGATION_BUTTON_WIDTH, Unit.PIXELS);
        outboundBtn.addStyleName("cellFontSizeOverride");
        outboundBtn.addClickListener(createNavigatorClickListener(ViewName.OUTBOUND));
        Button sendersBtn = new Button("Senders");
        sendersBtn.setWidth(NAVIGATION_BUTTON_WIDTH, Unit.PIXELS);
        sendersBtn.addStyleName("cellFontSizeOverride");
        sendersBtn.addClickListener(createNavigatorClickListener(ViewName.SENDERS));
        Button accessPointsBtn = new Button("Access points");
        accessPointsBtn.setWidth(NAVIGATION_BUTTON_WIDTH, Unit.PIXELS);
        accessPointsBtn.addStyleName("cellFontSizeOverride");
        accessPointsBtn.addClickListener(createNavigatorClickListener(ViewName.ACCESS_POINTS));
        Button statisticsBtn = new Button("Statistics");
        statisticsBtn.setWidth(NAVIGATION_BUTTON_WIDTH, Unit.PIXELS);
        statisticsBtn.addStyleName("cellFontSizeOverride");
        statisticsBtn.addClickListener(createNavigatorClickListener(ViewName.STATISTICS));
        Button advancedSearchBtn = new Button("Advanced search");
        advancedSearchBtn.setWidth(NAVIGATION_BUTTON_WIDTH, Unit.PIXELS);
        advancedSearchBtn.addStyleName("cellFontSizeOverride");
        advancedSearchBtn.addClickListener(createNavigatorClickListener(ViewName.ADVANCED_SEARCH));
        Button helpBtn = new Button("Help");
        helpBtn.setWidth(NAVIGATION_BUTTON_WIDTH, Unit.PIXELS);
        helpBtn.addStyleName("cellFontSizeOverride");
        helpBtn.addClickListener(createNavigatorClickListener(ViewName.HELP));

        leftMenu.addComponent(inboundBtn);
        leftMenu.addComponent(outboundBtn);
        leftMenu.addComponent(sendersBtn);
        leftMenu.addComponent(accessPointsBtn);
        leftMenu.addComponent(statisticsBtn);
        leftMenu.addComponent(advancedSearchBtn);
        leftMenu.addComponent(helpBtn);
    }

    private VerticalLayout initLeftMenu() {
        VerticalLayout leftMenu = new VerticalLayout();
        leftMenu.setMargin(new MarginInfo(false, false, true, true));
        leftMenu.setDefaultComponentAlignment(Alignment.TOP_CENTER);
        return leftMenu;
    }

    private HorizontalLayout initBody() {
        HorizontalLayout body = new HorizontalLayout();
        body.setSizeFull();
        body.setMargin(false);
        body.setDefaultComponentAlignment(Alignment.TOP_LEFT);
        return body;
    }

    private HorizontalLayout initHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidth(100, Unit.PERCENTAGE);
        header.setHeightUndefined();
        header.setMargin(new MarginInfo(false, false, false, false));
       /* Label headerLabel = new Label("OpusCapita Peppol Access Point Support Portal");
        header.addComponent(headerLabel);*/
        header.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        header.addLayoutClickListener((LayoutEvents.LayoutClickListener) event -> navigator.navigateTo(ViewName.HOME));

        Resource res = new ClassResource("/img/bg-header.png");
        Image image = new Image(null, res);
        header.addComponent(image);

        Button logoutBtn = new Button("Logout");
        logoutBtn.addClickListener((Button.ClickListener) event -> getPage().setLocation(baseUrl + "/logout"));
        header.addComponent(logoutBtn);
        header.setComponentAlignment(logoutBtn, Alignment.MIDDLE_RIGHT);

        return header;
    }

    private void initNavigator(VerticalLayout contentPanel) {
        navigator = new Navigator(this, contentPanel);
        navigator.addProvider(viewProvider);
    }

    Button.ClickListener createNavigatorClickListener(String destinationView) {
        return (Button.ClickListener) event -> {
            httpSession.setAttribute(SessionParams.LAST_VIEW, destinationView);
            navigator.navigateTo(destinationView);
        };
    }

}
