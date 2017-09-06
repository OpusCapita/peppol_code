package com.opuscapita.peppol.ui.portal.ui.views;

import com.opuscapita.peppol.ui.portal.ui.views.util.ViewUtil;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

@SpringView(name = ViewName.STATISTICS)
public class StatisticsView extends VerticalLayout implements View {
    @Autowired
    HttpSession httpSession;

    @PostConstruct
    void init() {
        setSizeFull();
        setDefaultComponentAlignment(Alignment.TOP_LEFT);
        setMargin(false);
        ViewUtil.updateSessionViewObject(httpSession, ViewName.STATISTICS);

        TabSheet statisticsTabSheet = new TabSheet();
        statisticsTabSheet.setSizeFull();
        statisticsTabSheet.setCaption("Statistics");
        statisticsTabSheet.addTab(new Label("IN 38200 OUT 212722"), "In/Out");
        statisticsTabSheet.addTab(new Label("Magical data here"), "Aggregated by senders");
        statisticsTabSheet.addTab(new Label("Magical data here"), "Aggregated by access point");
        statisticsTabSheet.addTab(new Label("Magical data here"), "Errors by access points");
        addComponent(statisticsTabSheet);
    }
}
