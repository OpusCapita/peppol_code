package com.opuscapita.peppol.ui.portal.ui.views.fragments.messages;

import com.opuscapita.peppol.commons.revised_model.Message;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.AbstractGridFragment;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.GridFragmentMode;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.GridFragmentType;
import com.vaadin.data.ValueProvider;
import com.vaadin.server.SerializableComparator;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MessagesGridFragment extends AbstractGridFragment {
    private final Grid<Message> grid;
    private final GridFragmentType direction;
    private final GridFragmentMode mode;

    public MessagesGridFragment(GridFragmentType direction, GridFragmentMode mode, MessagesLazyLoadService messagesLazyLoadService) {
        initLayout();
        this.direction = direction;
        this.mode = mode;
        grid = new Grid<>();
        grid.addColumn(Message::getId).setCaption("Id")
                .setSortable(true)
                .setSortProperty("id")
                .setComparator((SerializableComparator<Message>) (o1, o2) -> o1.getId().compareTo(o2.getId()))
                .setHidable(true);
        grid.addColumn(Message::getSender).setCaption("Sender")
                .setSortable(true)
                .setSortProperty("sender")
                .setHidable(true);
        grid.addColumn(Message::getRecipient).setCaption("Recipient")
                .setSortable(true)
                .setSortProperty("recipient")
                .setHidable(true);
        grid.addColumn((ValueProvider<Message, String>) message -> Instant.ofEpochMilli(message.getCreated()).atZone(ZoneId.systemDefault()).toLocalDateTime().toString())
                .setCaption("Date/time")
                .setSortProperty("created")
                .setSortable(true);
        grid.addComponentColumn((ValueProvider<Message, HorizontalLayout>) message -> {
            HorizontalLayout result = new HorizontalLayout();
            result.setSizeFull();
            result.setSpacing(true);
            HorizontalLayout infoBar = new HorizontalLayout();
            infoBar.setSizeFull();
            Label totalLabel = new Label("Total attempts: " + message.getAttempts().size());
            totalLabel.setVisible(true);
            infoBar.addComponent(totalLabel);
            HorizontalLayout actionBar = new HorizontalLayout();
            Button detailsBtn = new Button("Details");
            detailsBtn.addClickListener((Button.ClickListener) event -> {
                String messageDirection = message.isInbound() ? "Inbound" : "Outbound";
                final Window detailedView = new Window();
                detailedView.setCaptionAsHtml(true);
                detailedView.setCaption("<b>Details on " + messageDirection + " message:</b> " + message.getId());
                detailedView.setVisible(true);
                detailedView.setModal(true);
                detailedView.setResizable(false);
                detailedView.setHeight(400, Unit.PIXELS);
                detailedView.setWidth(800, Unit.PIXELS);
                VerticalLayout detailedContent = new VerticalLayout();
                detailedContent.setSizeUndefined();
                detailedContent.setSpacing(false);
                detailedContent.setMargin(true);
                HorizontalLayout participantsInfo = new HorizontalLayout();
                participantsInfo.setWidth(100, Unit.PERCENTAGE);
                participantsInfo.setHeightUndefined();
                Label senderLabel = new Label("<b>Sender:</b> " + message.getSender(), ContentMode.HTML);
                participantsInfo.addComponent(senderLabel);
                Label recipientLabel = new Label("<b>Recipient:</b> " + message.getRecipient(), ContentMode.HTML);
                participantsInfo.addComponent(recipientLabel);
                detailedContent.addComponent(participantsInfo);
                HorizontalLayout processingInfo = new HorizontalLayout();
                processingInfo.setWidth(100, Unit.PERCENTAGE);
                processingInfo.setHeightUndefined();
                Label processingStartLabel = new Label("<b>Processing started at:</b> " + Instant.ofEpochMilli(message.getCreated()).atZone(ZoneId.systemDefault()).toLocalDateTime(), ContentMode.HTML);
                processingInfo.addComponent(processingStartLabel);
                Label attemptsLabel = new Label("<b>Attempts:</b> " + message.getAttempts().size(), ContentMode.HTML);
                processingInfo.addComponent(attemptsLabel);
                detailedContent.addComponent(processingInfo);
                message.getAttempts().forEach(attempt -> {
                    VerticalLayout attemptDetails = new VerticalLayout();
                    attemptDetails.setSpacing(false);
                    attemptDetails.setMargin(false);
                    attemptDetails.setWidth(100, Unit.PERCENTAGE);
                    attemptDetails.setHeightUndefined();
                    Label attemptTimestampLabel = new Label("<b>Attempt:</b> " + Instant.ofEpochMilli(attempt.getId()).atZone(ZoneId.systemDefault()).toLocalDateTime(), ContentMode.HTML);
                    attemptDetails.addComponent(attemptTimestampLabel);
                    Label attemptFileNameLabel = new Label("<b>Filename:</b> " + attempt.getFilename(), ContentMode.HTML);
                    attemptDetails.addComponent(attemptFileNameLabel);
                    Grid<com.opuscapita.peppol.commons.revised_model.Event> eventsGrid = new Grid<>("Events: " + attempt.getEvents().size());
                    eventsGrid.setWidth(640, Unit.PIXELS);
                    eventsGrid.setHeightMode(HeightMode.ROW);
                    eventsGrid.setHeightByRows(5);
                    eventsGrid.addColumn((ValueProvider<com.opuscapita.peppol.commons.revised_model.Event, String>) eventItem -> Instant.ofEpochMilli(eventItem.getId()).atZone(ZoneId.systemDefault()).toLocalDateTime().toString())
                            .setCaption("When");
                    eventsGrid.addColumn(com.opuscapita.peppol.commons.revised_model.Event::getSource)
                            .setCaption("Source");
                    eventsGrid.addColumn(com.opuscapita.peppol.commons.revised_model.Event::getDetails)
                            .setCaption("Details");
                    eventsGrid.addColumn(com.opuscapita.peppol.commons.revised_model.Event::getStatus)
                            .setCaption("Status");
                    eventsGrid.addColumn((ValueProvider<com.opuscapita.peppol.commons.revised_model.Event, String>) eventItem -> eventItem.isTerminal() ? "yes" : "no")
                            .setCaption("Final");
                    eventsGrid.setItems(attempt.getEvents());
                    attemptDetails.addComponent(eventsGrid);
                    detailedContent.addComponent(attemptDetails);
                });
                detailedView.setContent(detailedContent);
                detailedView.addCloseListener(new Window.CloseListener() {
                    @Override
                    public void windowClose(Window.CloseEvent e) {
                        detailedView.setModal(false);
                        detailedView.setVisible(false);
                        getUI().removeWindow(detailedView);
                    }
                });
                getUI().addWindow(detailedView);
            });
            actionBar.addComponent(detailsBtn);
            Button downloadBtn = new Button("Download");
            downloadBtn.addClickListener((Button.ClickListener) event -> {
                if (message.getAttempts().size() == 0) {
                    //Show message about no files? :) Makes no sense, but error message has to be shown
                } else if (message.getAttempts().size() == 1) {
                    //Download the file right away;
                } else {
                    //Show dialog with links to file per each attempt
                }
            });
            actionBar.addComponent(downloadBtn);
            result.addComponent(infoBar);
            result.addComponent(actionBar);
            return result;
        }).setCaption("Attempts");
        grid.setSizeFull();

        AtomicBoolean isInbound = new AtomicBoolean(false);
        AtomicInteger count = new AtomicInteger(0);
        switch (direction) {
            case INBOUND:
                isInbound.set(true);
                break;
            case OUTBOUND:
                isInbound.set(false);
                break;
            default:
                throw new IllegalStateException("This component is supposed to handle only INBOUND and OUTBOUND as directions");
        }
        count.set(messagesLazyLoadService.countByInbound(isInbound.get()));
        grid.setDataProvider(
                (sortOrders, offset, limit) -> {
                    Map<String, Boolean> sortOrder = sortOrders.stream()
                            .collect(Collectors.toMap(
                                    sort -> sort.getSorted(),
                                    sort -> sort.getDirection() == SortDirection.ASCENDING));

                    return messagesLazyLoadService.findByInbound(isInbound.get(), offset, limit, sortOrder).stream();
                },
                () -> count.get()
        );

        addComponent(grid);
        /*BackEndDataProvider backEndDataProvider;
        grid.setItems(repository.findMessagesByInbound(isInbound));*/

    }

    public String getTag() {
        return direction + "_" + mode;
    }
}
