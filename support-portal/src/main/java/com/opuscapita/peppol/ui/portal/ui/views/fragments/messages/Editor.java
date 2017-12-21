package com.opuscapita.peppol.ui.portal.ui.views.fragments.messages;

import com.opuscapita.peppol.commons.revised_model.Message;
import com.opuscapita.peppol.ui.portal.util.FileService;
import com.vaadin.event.selection.MultiSelectionEvent;
import com.vaadin.event.selection.MultiSelectionListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;

import java.util.HashSet;
import java.util.Set;

public class Editor extends VerticalLayout implements MultiSelectionListener<Message> {
    private Set<Message> selectedMessages;
    Button reprocessAllBtn;

    public Editor(Grid<Message> grid, FileService fileService) {
        selectedMessages = new HashSet<>();
        reprocessAllBtn = new Button("Reprocess all");
        reprocessAllBtn.addClickListener(event -> selectedMessages.forEach(m -> fileService.reprocess(m.getAttempts().last())));
        addComponent(reprocessAllBtn);
    }

    @Override
    public void selectionChange(MultiSelectionEvent<Message> event) {
        selectedMessages = event.getAllSelectedItems();
        setEnabled(!event.getAllSelectedItems().isEmpty());
    }
}
