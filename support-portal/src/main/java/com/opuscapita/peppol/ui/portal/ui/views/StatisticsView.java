package com.opuscapita.peppol.ui.portal.ui.views;

import com.opuscapita.peppol.ui.portal.model.MessagesRepository;
import com.opuscapita.peppol.ui.portal.ui.views.util.ViewUtil;
import com.vaadin.navigator.View;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@SpringView(name = ViewName.STATISTICS)
public class StatisticsView extends VerticalLayout implements View {
    @Autowired
    HttpSession httpSession;

    @Autowired
    MessagesRepository messagesRepository;

    @PostConstruct
    void init() {
        setSizeFull();
        setDefaultComponentAlignment(Alignment.TOP_LEFT);
        setMargin(false);
        ViewUtil.updateSessionViewObject(httpSession, ViewName.STATISTICS);

        TabSheet statisticsTabSheet = new TabSheet();
        statisticsTabSheet.setSizeFull();
        statisticsTabSheet.setCaption("Statistics");
        statisticsTabSheet.addTab(getStatisticsContent(), "In/Out");
        statisticsTabSheet.addTab(new Label("Magical data here"), "Aggregated by senders");
        statisticsTabSheet.addTab(new Label("Magical data here"), "Aggregated by access point");
        statisticsTabSheet.addTab(new Label("Magical data here"), "Errors by access points");
        addComponent(statisticsTabSheet);
    }

    @NotNull
    private Component getStatisticsContent() {
        VerticalLayout content = new VerticalLayout();
        content.setSizeUndefined();
        HorizontalLayout overallInfo = new HorizontalLayout();
        overallInfo.setCaptionAsHtml(true);
        overallInfo.setCaption("Overall:");
        overallInfo.setSizeFull();
        Label overallIn = new Label("<b>IN:</b> " + messagesRepository.countMessagesByInbound(true), ContentMode.HTML);
        overallInfo.addComponent(overallIn);
        Label overallOut = new Label("<b>OUT:</b> " + messagesRepository.countMessagesByInbound(false), ContentMode.HTML);
        overallInfo.addComponent(overallOut);
        content.addComponent(overallInfo);
        HorizontalLayout currentMonthInfo = new HorizontalLayout();
        currentMonthInfo.setCaptionAsHtml(true);
        currentMonthInfo.setCaption("Current month:");
        currentMonthInfo.setSizeFull();
        System.out.println("Month start: " + getCurrentMonthStartEpoch());
        System.out.println("Next month start: " + getNextMonthStartEpoch());
        Label currentMonthIn = new Label("<b>IN:</b> " + messagesRepository.countMessagesByInboundAndCreatedBetween(true, getCurrentMonthStartEpoch(), getNextMonthStartEpoch()), ContentMode.HTML);
        currentMonthInfo.addComponent(currentMonthIn);
        Label currentMonthOut = new Label("<b>OUT:</b> " + messagesRepository.countMessagesByInboundAndCreatedBetween(false, getCurrentMonthStartEpoch(), getNextMonthStartEpoch()), ContentMode.HTML);
        currentMonthInfo.addComponent(currentMonthOut);
        content.addComponent(currentMonthInfo);
        return content;
    }

    private long getCurrentMonthStartEpoch() {
        LocalDateTime now = LocalDateTime.now();
        String month = String.valueOf(now.getMonthValue());
        if (month.length() < 2) {
            month = "0" + month;
        }

        String year = String.valueOf(now.getYear());
        String stuffToParse = "01." + month + "." + year + "  00:00:01";

        System.out.println(stuffToParse);
        LocalDateTime time = LocalDateTime.parse(/*"04.02.2014  19:51:01"*/stuffToParse, DateTimeFormatter.ofPattern("dd.MM.yyyy  HH:mm:ss"));
        return time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
    }

    private long getNextMonthStartEpoch() {
        LocalDateTime now = LocalDateTime.now();
        String month = String.valueOf(now.getMonthValue() == 12 ? 1 : now.getMonthValue() + 1);

        if (month.length() < 2) {
            month = "0" + month;
        }

        String year = String.valueOf(now.getYear());
        String stuffToParse = "01." + month + "." + year + "  00:00:00";

        System.out.println(stuffToParse);
        LocalDateTime time = LocalDateTime.parse(/*"04.02.2014  19:51:01"*/stuffToParse, DateTimeFormatter.ofPattern("dd.MM.yyyy  HH:mm:ss"));
        return time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
    }

}
