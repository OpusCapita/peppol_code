package com.opuscapita.peppol.ui.portal.ui.views.fragments.messages;

import com.vaadin.ui.*;

import java.util.function.Consumer;

public class ConfirmDialog extends Window {
    private String message = "Are you sure?";
    private final Consumer callback;

    public ConfirmDialog(Consumer callback) {
        this.callback = callback;
    }

    public ConfirmDialog(String caption, Consumer callback) {
        super(caption);
        this.callback = callback;
    }

    public ConfirmDialog(String caption, Component content, Consumer callback) {
        super(caption, content);
        this.callback = callback;
    }

    public ConfirmDialog(String caption, String message, Consumer callback) {
        super(caption);
        this.callback = callback;
        this.message = message;
    }
    public ConfirmDialog(String caption, String message, Component component, Consumer callback) {
        super(caption, component);
        this.callback = callback;
        this.message = message;
    }

    public ConfirmDialog buildAndShow() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        Label messageLabel = new Label();
        messageLabel.setCaptionAsHtml(true);
        messageLabel.setCaption(message);
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button yesButton = new Button("Yes");
        yesButton.addClickListener((Button.ClickListener) event -> {
            if (callback != null ) {
                ConfirmDialog.this.callback.accept(null);
            }
            ConfirmDialog.this.close();
        });
        Button noButton = new Button("No");
        noButton.addClickListener((Button.ClickEvent event) -> ConfirmDialog.this.close());
        buttonLayout.addComponents(yesButton, noButton);
        mainLayout.addComponents(messageLabel, buttonLayout);
        this.addCloseListener((CloseListener) e -> {
            ConfirmDialog.this.setModal(false);
            ConfirmDialog.this.setVisible(false);
            UI.getCurrent().removeWindow(ConfirmDialog.this);
        });
        setContent(mainLayout);
        UI.getCurrent().addWindow(this);
        return this;
    }
}
