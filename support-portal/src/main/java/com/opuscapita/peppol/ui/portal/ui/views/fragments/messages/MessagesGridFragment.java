package com.opuscapita.peppol.ui.portal.ui.views.fragments.messages;

import com.opuscapita.peppol.commons.revised_model.Attempt;
import com.opuscapita.peppol.commons.revised_model.Message;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.AbstractGridFragment;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.GridFragmentMode;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.GridFragmentType;
import com.opuscapita.peppol.ui.portal.ui.views.util.AdvancedFileDownloader;
import com.opuscapita.peppol.ui.portal.util.FileService;
import com.vaadin.data.HasValue;
import com.vaadin.data.TreeData;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.*;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.components.grid.MultiSelectionModel;
import com.vaadin.ui.components.grid.MultiSelectionModelImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MessagesGridFragment extends AbstractGridFragment {
    private static final Logger logger = LoggerFactory.getLogger(MessagesGridFragment.class);

    private final Grid<Message> grid;
    private final GridFragmentType direction;
    private final GridFragmentMode mode;
    private final HorizontalLayout filterBar;
    private final MessagesLazyLoadService messagesLazyLoadService;
    private FileService fileService;

    public MessagesGridFragment(GridFragmentType direction, GridFragmentMode mode, MessagesLazyLoadService messagesLazyLoadService, FileService fileService) {
        this.fileService = fileService;
        this.messagesLazyLoadService = messagesLazyLoadService;
        initLayout();
        this.direction = direction;
        this.mode = mode;
        grid = new Grid<>();
        setupGridColumns();
        grid.setSizeFull();
        grid.addStyleName("cellFontSizeOverride");

        String keyWord = getKeyWordByMode(mode);
        boolean inbound = direction == GridFragmentType.INBOUND;
        Map<String, String> filters = prepareBasicFilters(mode, keyWord, inbound);

        setDataProvider(filters);

        filterBar = new HorizontalLayout();
        setupFilterBar(filters);
        filterBar.setSizeUndefined();
        filterBar.setMargin(false);
        filterBar.setVisible(true);
        addComponent(filterBar);
        addComponent(grid);

        MultiSelectionModelImpl<Message> selectionModel = (MultiSelectionModelImpl<Message>) grid.setSelectionMode(Grid.SelectionMode.MULTI);
        selectionModel.setSelectAllCheckBoxVisibility(MultiSelectionModel.SelectAllCheckBoxVisibility.HIDDEN);
        Editor messageEditor = new Editor(fileService);
        grid.asMultiSelect().addSelectionListener(messageEditor);

        addComponent(messageEditor);

        setExpandRatio(filterBar, 1);
        setExpandRatio(grid, 10);
        setExpandRatio(messageEditor, 1);
    }

    protected void setDataProvider(Map<String, String> filters) {
        StringBuilder filterInfo = new StringBuilder("Setting data provider with following filters:\n");
        filters.entrySet().forEach(entry -> filterInfo.append("\t").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n"));
        logger.info(filterInfo.toString());
        grid.setDataProvider(
                (sortOrders, offset, limit) -> {
                    Map<String, Boolean> sortOrder = sortOrders.stream()
                            .collect(Collectors.toMap(
                                    sort -> sort.getSorted(),
                                    sort -> sort.getDirection() == SortDirection.ASCENDING));
                    return prepareGridData(filters, offset, limit, sortOrder);
                },
                () -> getGridDataCount(filters)
        );
    }

    private void setupFilterBar(Map<String, String> filters) {
        /*ArrayList<Map.Entry> fieldsList = new ArrayList<>();
        grid.getColumns().stream().filter(column -> column.isSortable() && column.getId() != null).forEach(column -> fieldsList.add(new AbstractMap.SimpleEntry(column.getId(), column.getCaption())));
        Label fieldLabel = new Label("Select filter field");
        filterBar.addComponent(fieldLabel);
        ComboBox<Map.Entry> fieldSelect = new ComboBox<>();
        fieldSelect.setItems(fieldsList);
        fieldSelect.setWidth(240, Unit.PIXELS);
        fieldSelect.setItemCaptionGenerator((ItemCaptionGenerator<Map.Entry>) item -> item.getValue().toString());
        filterBar.addComponent(fieldSelect);
        TextField filterValue = new TextField();
        filterValue.setPlaceholder("Enter filter criteria value, please...");
        filterValue.setWidth(300, Unit.PIXELS);
        filterBar.addComponent(filterValue);
        Button performFilteringButton = new Button("Go!!!");
        performFilteringButton.addClickListener((Button.ClickListener) event -> {
            //Cast filtering spell magic here!!!
        });
        filterBar.addComponent(performFilteringButton);*/
        /*grid.getColumns()
                .stream()
                .forEach(column -> {
                    TextField filterValue = new TextField();
                    filterValue.setId(column.getId());
                    filterValue.setWidth((float) column.getWidth(), Unit.PIXELS);
                    filterValue.setReadOnly(!(column.isSortable() && column.getId() != null));
                    filterBar.addComponent(filterValue);
                });*/
        HeaderRow headerRow = grid.prependHeaderRow();
        grid.getColumns()
                .stream()
                .filter(column -> column.isSortable() && column.getId() != null)
                .forEach(column -> {
                    AbstractComponent filterField;
                    if (column.getId().equalsIgnoreCase("document_type")) {
                        filterField = new ComboBox<>();
                        ((ComboBox) filterField).setDataProvider(new ListDataProvider(messagesLazyLoadService.findDocumentTypes()));
                        ((ComboBox) filterField).addValueChangeListener((HasValue.ValueChangeListener) event -> updateFilter(filters, column.getId(), event.getValue() == null ? "" : event.getValue().toString()));
                    } else {
                        filterField = new TextField();
                        ((TextField) filterField).addValueChangeListener((HasValue.ValueChangeListener<String>) event -> {
                            updateFilter(filters, column.getId(), event.getValue());
                        });
                    }
                    filterField.setHeight(32, Unit.PIXELS);
                    filterField.setWidth(100, Unit.PERCENTAGE);

                    headerRow.getCell(column.getId()).setComponent(filterField);
                });
    }

    private void updateFilter(Map<String, String> filters, String id, String value) {
        System.out.println("Update filter for " + id + " with value: " + value);
        filters.put(id, value);
        setDataProvider(filters);
    }

    private int getGridDataCount(Map<String, String> filters) {
        return messagesLazyLoadService.countByFilter(filters);
    }

    @NotNull
    private Map<String, String> prepareBasicFilters(GridFragmentMode mode, String keyWord, boolean inbound) {
        Map<String, String> filters = new HashMap<>();
        filters.put("inbound", String.valueOf(inbound));
        if (mode == GridFragmentMode.ALL) {
            //return messagesLazyLoadService.countByInbound(inbound);
        } else {
            boolean terminal = mode != GridFragmentMode.REPROCESSING;
            filters.put("terminal", String.valueOf(terminal));
            if (keyWord != null) {
                filters.put("status", keyWord);
            }
            /*if (keyWord == null) {
                return messagesLazyLoadService.countByInboundAndTerminal(inbound, terminal);
            } else {
                return messagesLazyLoadService.countByInboundAndTerminalAndStatusContains(inbound, terminal, keyWord);
            }*/
        }
        return filters;
    }

    private Stream<Message> prepareGridData(Map<String, String> filters, int offset, int limit, Map<String, Boolean> sortOrder) {
        return messagesLazyLoadService.findByFilter(filters, offset, limit, sortOrder).stream();
    }

    @Nullable
    private String getKeyWordByMode(GridFragmentMode mode) {
        String keyWord = null;
        switch (mode) {
            case DELIVERED:
                keyWord = "delivered";
                break;
            case ALL:
                keyWord = null;
                break;
            case FAILED:
                keyWord = "failed";
                break;
            case REJECTED:
                keyWord = "failed with I/O error";
                break;
            case REPROCESSING:
                keyWord = "reprocessing";
                break;
        }
        return keyWord;
    }

    private void setupGridColumns() {
        grid.addColumn((ValueProvider<Message, String>) message -> {
            try {
                return message.getId().split("_", 2)[1];
            } catch (Exception e) {
                return message.getId();
            }
        }).setCaption("Id")
                .setSortable(true)
                .setSortProperty("id")
                .setId("id")
                .setComparator((SerializableComparator<Message>) (o1, o2) -> o1.getId().compareTo(o2.getId()))
                .setHidable(true);
        grid.addColumn(Message::getSender).setCaption("Sender")
                .setSortable(true)
                .setId("sender")
                .setSortProperty("sender")
                .setHidable(true);
        grid.addColumn(Message::getRecipient).setCaption("Recipient")
                .setSortable(true)
                .setId("recipient")
                .setSortProperty("recipient")
                .setHidable(true);
        grid.addColumn(Message::getDocumentType).setCaption("Document type")
                .setSortable(true)
                .setId("document_type")
                .setSortProperty("documentType")
                .setHidable(true);
        grid.addColumn(Message::getDocumentNumber).setCaption("Document number")
                .setSortable(true)
                .setId("document_number")
                .setSortProperty("documentNumber")
                .setHidable(true);
        grid.addColumn(Message::getDocumentDate).setCaption("Document date")
                .setSortable(true)
                .setId("document_date")
                .setSortProperty("documentDate")
                .setHidable(true);

        grid.addColumn(Message::getDocumentDate).setCaption("Due date")
                .setSortable(true)
                .setId("due_date")
                .setSortProperty("dueDate")
                .setHidable(true);

        grid.addColumn((ValueProvider<Message, String>) message ->
                Instant.ofEpochMilli(message.getCreated()).atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .setCaption("Created")
                .setId("created")
                .setSortProperty("created")
                .setSortable(true);
        grid.addColumn((ValueProvider<Message, String>) message -> String.valueOf(message.getAttempts().stream().flatMap(attempt -> attempt.getEvents().stream()).filter(event -> event.isTerminal()).count()))
                .setCaption("Delivered (times)")
                .setId("delivered")
                .setSortable(false)
                .setHidable(true);
        grid.addColumn((ValueProvider<Message, String>) Message::getLastStatus)
                .setCaption("Last status")
                .setId("lastStatus")
                .setSortable(false)
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

            /*Button downloadBtn = createDownloadButton(message.getAttempts().last());
            if (downloadBtn.isEnabled()) {
                downloadBtn.setDescription("Latest file will be downloaded");
            }
            actionBar.addComponent(downloadBtn);

            Button reprocessBtn = createReprocessButton(message.getAttempts().last());
            actionBar.addComponent(reprocessBtn);*/


            result.addComponent(actionBar);
            return result;
        }).setCaption("").setSortable(false);
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

            /*Download functionality*/
            Button reprocessBtn = createReprocessButton(attempt);

            Button downloadBtn = createDownloadButton(attempt);
            detailedContent.addComponent(downloadBtn);

            /*Reprocess functionality*/
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

    private Button createReprocessButton(Attempt attempt) {
        Button reprocessBtn = new Button("Reprocess");
        File fileToOperate = new File(attempt.getFilename());
        if (!fileToOperate.exists()) {
            reprocessBtn.setEnabled(false);
            reprocessBtn.setComponentError(new UserError("File not available for reprocess!"));
        }

        reprocessBtn.addClickListener((Button.ClickListener) event -> {

            new ConfirmDialog(
                    "Confirmation",
                    "Reprocess " + new File(attempt.getFilename()).getName() + " ?",
                    o -> fileService.reprocess(attempt)
            ).buildAndShow();
        });
        return reprocessBtn;
    }

    private Button createDownloadButton(Attempt attempt) {
        Button downloadBtn = new Button("Download");
        File fileToOperate = new File(attempt.getFilename());
        if (fileToOperate.exists()) {
            Resource resource = new FileResource(fileToOperate);
            new FileDownloader(resource).extend(downloadBtn);
        } else {
            AdvancedFileDownloader downloader = new AdvancedFileDownloader();  //have to create own downloader because vaadin doesn't support download in runtime
            downloader.addAdvancedDownloaderListener(downloadEvent -> {
                File fileToDownload = fileService.extractFromArchive(attempt);
                if (fileToDownload != null) {
                    downloader.setFilePath(fileToDownload.getAbsolutePath());                     //setting filepath in runtime
                } else {
                    NotificationDialog error = new NotificationDialog("Sorry", "File not available!");
                    error.buildAndShow();
                }

            });
            downloader.extend(downloadBtn);
        }
        return downloadBtn;
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
