package com.opuscapita.peppol.ui.portal.ui.views.fragments.messages;

import com.vaadin.ui.*;

public class NotificationDialog extends Window {

    private String message = "";

    public NotificationDialog(String caption, String message) {
        super(caption);
        this.message = message;
    }

    public NotificationDialog buildAndShow() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        Label messageLabel = new Label();
        messageLabel.setCaptionAsHtml(true);
        messageLabel.setCaption(message);
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button okButton = new Button("OK");
        okButton.addClickListener((Button.ClickListener) event -> {
            NotificationDialog.this.close();
        });
        buttonLayout.addComponent(okButton);
        mainLayout.addComponents(messageLabel, buttonLayout);
        this.addCloseListener((CloseListener) e -> {
            NotificationDialog.this.setModal(false);
            NotificationDialog.this.setVisible(false);
            UI.getCurrent().removeWindow(NotificationDialog.this);
        });
        setResizable(false);
        setHeight(200, Unit.PIXELS);
        setWidth(500, Unit.PIXELS);
        center();
        setContent(mainLayout);
        UI.getCurrent().addWindow(this);
        return this;
    }
}
