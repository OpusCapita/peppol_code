package com.opuscapita.peppol.ui.portal.ui.views.fragments.messages;

import com.opuscapita.peppol.commons.revised_model.Message;
import com.opuscapita.peppol.ui.portal.util.FileService;
import com.vaadin.event.selection.MultiSelectionEvent;
import com.vaadin.event.selection.MultiSelectionListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;

import java.util.HashSet;
import java.util.Set;

/**
 * Layout holding functionality such as buttons for manipulating selected messages, placed below message grid
 */
public class Editor extends HorizontalLayout implements MultiSelectionListener<Message> {
    private Set<Message> selectedMessages;
    Button reprocessAllBtn;

    public Editor(FileService fileService) {
        selectedMessages = new HashSet<>();

        setHeight(20,Unit.PIXELS);

        reprocessAllBtn = new Button("Reprocess all");
        reprocessAllBtn.addClickListener(event -> selectedMessages.forEach(m -> fileService.reprocess(m.getAttempts().last())));
        reprocessAllBtn.setEnabled(false);
        addComponent(reprocessAllBtn);
        setComponentAlignment(reprocessAllBtn, Alignment.BOTTOM_LEFT);
    }

    @Override
    public void selectionChange(MultiSelectionEvent<Message> event) {
        selectedMessages = event.getAllSelectedItems();
        reprocessAllBtn.setEnabled(!event.getAllSelectedItems().isEmpty());
    }
}
