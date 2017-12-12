package com.opuscapita.peppol.ui.portal.ui.views.fragments;

import com.vaadin.ui.Grid;

import java.util.Collection;

public class PlainGridFragment extends AbstractGridFragment {
    private final Grid grid;
    private final GridFragmentType direction;
    private final GridFragmentMode mode;

    public PlainGridFragment(GridFragmentType direction, GridFragmentMode mode, Class itemClass, Collection items) {
        initLayout();
        this.direction = direction;
        this.mode = mode;
        grid = new Grid<>(itemClass);
        grid.setSizeFull();
        addComponent(grid);
        grid.setItems(items);
    }

    public String getTag() {
        return direction + "_" + mode;
    }

    public Grid getGrid() {
        return grid;
    }
}
