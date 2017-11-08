package com.opuscapita.peppol.ui.portal.ui.views.fragments;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;

public abstract class AbstractGridFragment extends VerticalLayout {
    protected void initLayout() {
        setSizeFull();
        setDefaultComponentAlignment(Alignment.TOP_LEFT);
        setMargin(false);
    }
}
