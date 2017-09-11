package com.opuscapita.peppol.ui.portal.ui.views.fragments;

import com.opuscapita.peppol.ui.portal.model.Record;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GridFragment extends VerticalLayout {
    private final Grid grid;
    private final GridFragmentType direction;
    private final GridFragmentMode mode;


    public GridFragment(GridFragmentType direction, GridFragmentMode mode) {
        setSizeFull();
        setDefaultComponentAlignment(Alignment.TOP_LEFT);
        setMargin(false);

        this.direction = direction;
        this.mode = mode;
        grid = new Grid<>(Record.class);
        grid.setSizeFull();
        addComponent(grid);
        update();
    }

    public void update() {
        List<Record> items = IntStream.rangeClosed(1, 100).mapToObj(i -> new Record("name" + i, "surname" + i, "phone" + i, "data " + direction + ":" + mode)).collect(Collectors.toList());
        grid.setItems(items);
        grid.setCaption("Total: " + items.size());
    }

    public String getTag() {
        return direction + "_" + mode;
    }
}
