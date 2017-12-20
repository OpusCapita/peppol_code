package com.opuscapita.peppol.ui.portal.ui.views.fragments.messages;

import com.opuscapita.peppol.commons.revised_model.Message;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.AbstractGridFragment;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.GridFragmentMode;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.GridFragmentType;
import com.opuscapita.peppol.ui.portal.ui.views.util.AdvancedFileDownloader;
import com.opuscapita.peppol.ui.portal.util.FileService;
import com.vaadin.data.TreeData;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.*;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MessagesGridFragment extends AbstractGridFragment {
    private static final Logger logger = LoggerFactory.getLogger(MessagesGridFragment.class);

    private final Grid<Message> grid;
    private final GridFragmentType direction;
    private final GridFragmentMode mode;
    private FileService fileService;

    public MessagesGridFragment(GridFragmentType direction, GridFragmentMode mode, MessagesLazyLoadService messagesLazyLoadService, FileService fileService) {
        this.fileService = fileService;
        initLayout();
        this.direction = direction;
        this.mode = mode;
        grid = new Grid<>();
        setupGridColumns();
        grid.setSizeFull();

        AtomicBoolean isInbound = new AtomicBoolean(false);
        AtomicBoolean isTerminal = new AtomicBoolean(false);
        AtomicBoolean all = new AtomicBoolean(false);
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
        switch (mode) {
            case DELIVERED:
                isTerminal.set(true);
                break;
            case ALL:
                all.set(true);
                break;
        }
        count.set(all.get() ? messagesLazyLoadService.countByInbound(isInbound.get()) : messagesLazyLoadService.countByInboundAndTerminal(isInbound.get(), isTerminal.get()));
        grid.setDataProvider(
                (sortOrders, offset, limit) -> {
                    Map<String, Boolean> sortOrder = sortOrders.stream()
                            .collect(Collectors.toMap(
                                    sort -> sort.getSorted(),
                                    sort -> sort.getDirection() == SortDirection.ASCENDING));

                    return all.get() ? messagesLazyLoadService.findByInbound(isInbound.get(), offset, limit, sortOrder).stream() : messagesLazyLoadService.findByInboundAndTerminal(isInbound.get(), isTerminal.get(), offset, limit, sortOrder).stream();
                },
                () -> count.get()
        );

        addComponent(grid);
        /*BackEndDataProvider backEndDataProvider;
        grid.setItems(repository.findMessagesByInbound(isInbound));*/

    }

    private void setupGridColumns() {
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
        grid.addColumn((ValueProvider<Message, String>) message ->
                Instant.ofEpochMilli(message.getCreated()).atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .setCaption("Created")
                .setSortProperty("created")
                .setSortable(true);
        grid.addColumn((ValueProvider<Message, String>) message -> String.valueOf(message.getAttempts().stream().flatMap(attempt -> attempt.getEvents().stream()).filter(event -> event.isTerminal()).count()))
                .setCaption("Delivered (times)")
                .setHidable(true);
        grid.addColumn((ValueProvider<Message, String>) Message::getLastStatus)
                .setCaption("Last status")
                .setStyleGenerator((StyleGenerator<Message>) msg -> {
                    switch (msg.getLastStatus().toLowerCase()) {
                        case "delivered":
                            return "green-pls";
                        case "failed":
                        case "rejected":
                        case "invalid":
                            return "red-pls";
                    }
                    return "blue-pls";
                })
                .setHidable(true);
        grid.addComponentColumn((ValueProvider<Message, HorizontalLayout>) message -> {
            HorizontalLayout result = new HorizontalLayout();
            result.setSizeFull();
            result.setSpacing(true);
            HorizontalLayout actionBar = new HorizontalLayout();
            Button detailsBtn = new Button("Details");
            detailsBtn.addClickListener((Button.ClickListener) event -> showDetails(message));
            actionBar.addComponent(detailsBtn);
            result.addComponent(actionBar);
            return result;
        }).setCaption("");
    }

    protected void showDetails(Message message) {
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
        detailedContent.setSizeFull();
        detailedContent.setSpacing(true);
        detailedContent.setMargin(true);
        HorizontalLayout participantsInfo = new HorizontalLayout();
        participantsInfo.setWidth(100, Unit.PERCENTAGE);
        participantsInfo.setHeightUndefined();
        Label senderLabel = new Label("<b>Sender:</b> " + message.getSender(), ContentMode.HTML);
        participantsInfo.addComponent(senderLabel);
        Label recipientLabel = new Label("<b>Recipient:</b> " + message.getRecipient(), ContentMode.HTML);
        participantsInfo.addComponent(recipientLabel);
        detailedContent.addComponent(participantsInfo);
        detailedContent.setExpandRatio(participantsInfo, 1);
        HorizontalLayout processingInfo = new HorizontalLayout();
        processingInfo.setWidth(100, Unit.PERCENTAGE);
        processingInfo.setHeightUndefined();
        Label processingStartLabel = new Label("<b>Processing started at:</b> " + Instant.ofEpochMilli(message.getCreated()).atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), ContentMode.HTML);
        processingInfo.addComponent(processingStartLabel);
        Label attemptsLabel = new Label("<b>Attempts:</b> " + message.getAttempts().size(), ContentMode.HTML);
        processingInfo.addComponent(attemptsLabel);
        detailedContent.addComponent(processingInfo);
        detailedContent.setExpandRatio(processingInfo, 1);
        Tree<String> attemptDetails = new Tree<>();
        attemptDetails.setSizeFull();
        TreeData<String> attemptDetailsData = new TreeData<>();
        message.getAttempts().forEach(attempt -> {
            String attemptInfo = Instant.ofEpochMilli(extractAttemptCreationTimestamp(attempt.getId())).atZone(ZoneId.systemDefault()).toLocalDateTime().toString() + " " + attempt.getFilename();
            attemptDetailsData.addItem(null, attemptInfo);
            attempt.getEvents().forEach(event -> {
                String eventInfo = Instant.ofEpochMilli(event.getId()).atZone(ZoneId.systemDefault()).toLocalDateTime().toString();
                eventInfo += " " + event.getSource() + " " + event.getDetails() + " " + (event.isTerminal() ? "FINAL" : "");
                attemptDetailsData.addItem(attemptInfo, eventInfo);
            });
            /*VerticalLayout attemptDetails = new VerticalLayout();
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
            detailedContent.addComponent(attemptDetails);*/
            /*Download functionality*/
            Button downloadBtn = new Button("Download");
            Button reprocessBtn = new Button("Reprocess");

            File fileToOperate = new File(attempt.getFilename());

            if (fileToOperate.exists()) {  //file exists, adding as resource
                Resource resource = new FileResource(fileToOperate);
                new FileDownloader(resource).extend(downloadBtn);
            } else {
                reprocessBtn.setEnabled(false);
                reprocessBtn.setComponentError(new UserError("File not available for reprocess!"));
                if (fileService.fileArchived(attempt.getFilename())) { //file can be fetched from archive in runtime only, download via listener
                    AdvancedFileDownloader downloader = new AdvancedFileDownloader();  //have to create own downloader because vaadin doesn't support download in runtime
                    downloader.addAdvancedDownloaderListener(downloadEvent -> {
                        File fileToDownload = fileService.extractFromArchive(attempt.getFilename());  //tmp file
                        downloader.setFilePath(fileToDownload.getAbsolutePath());                     //setting filepath in runtime
                    });
                    downloader.extend(downloadBtn);
                } else {
                    downloadBtn.setEnabled(false);
                    downloadBtn.setComponentError(new UserError("File not available for download!"));
                }
            }
            detailedContent.addComponent(downloadBtn);

            /*Reprocess functionality*/
            reprocessBtn.addClickListener((Button.ClickListener) event -> fileService.reprocessFile(attempt));
            detailedContent.addComponent(reprocessBtn);

        });
        attemptDetails.setDataProvider(new TreeDataProvider<>(attemptDetailsData));
        detailedContent.addComponent(attemptDetails);
        detailedContent.setExpandRatio(attemptDetails, 5);
        detailedView.setContent(detailedContent);
        detailedView.addCloseListener((Window.CloseListener) e -> {
            detailedView.setModal(false);
            detailedView.setVisible(false);
            getUI().removeWindow(detailedView);
        });
        getUI().addWindow(detailedView);
    }

    private long extractAttemptCreationTimestamp(String id) {
        long result = System.currentTimeMillis();
        try {
            return Long.valueOf(id.split("_", 2)[0]);
        } catch (Exception e) {
            try {
                return Long.valueOf(id);
            } catch (Exception e1) {
                e = e1;
            }
            logger.warn("Failed extracting timestamp from attempt id: " + id);
            e.printStackTrace();
        }
        return result;
    }

    public String getTag() {
        return direction + "_" + mode;
    }
}
