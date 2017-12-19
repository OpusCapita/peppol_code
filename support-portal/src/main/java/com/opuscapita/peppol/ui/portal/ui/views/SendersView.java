package com.opuscapita.peppol.ui.portal.ui.views;

import com.opuscapita.peppol.commons.revised_model.Participant;
import com.opuscapita.peppol.ui.portal.model.ParticipantRepository;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.GridFragmentMode;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.GridFragmentType;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.PlainGridFragment;
import com.opuscapita.peppol.ui.portal.ui.views.util.ViewUtil;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SpringView(name = ViewName.SENDERS)
public class SendersView extends VerticalLayout implements View {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    HttpSession httpSession;

    @Autowired
    ParticipantRepository participantRepository;

    @PostConstruct
    @SuppressWarnings("unchecked")
    void init() {
        setSizeFull();
        setDefaultComponentAlignment(Alignment.TOP_LEFT);
        setMargin(false);
        ViewUtil.updateSessionViewObject(httpSession, ViewName.SENDERS);

        PlainGridFragment sendersGridFragment = new PlainGridFragment(GridFragmentType.SENDERS, GridFragmentMode.ALL, Participant.class, getSenders());
        Grid grid = sendersGridFragment.getGrid();
        setupGridOrdering(grid);
        sendersGridFragment.setCaption("Senders");
        ((Grid.Column<Participant, String>) grid.getColumn("outboundEmails")).setEditorComponent(new TextField(), Participant::setOutboundEmails);
        ((Grid.Column<Participant, String>) grid.getColumn("inboundEmails")).setEditorComponent(new TextField(), Participant::setInboundEmails);
        ((Grid.Column<Participant, String>) grid.getColumn("contactPerson")).setEditorComponent(new TextField(), Participant::setContactPerson);
        ((Grid.Column<Participant, String>) grid.getColumn("responsiblePerson")).setEditorComponent(new TextField(), Participant::setResponsiblePerson);
        grid.getEditor().setEnabled(true);
        grid.getEditor().addSaveListener(event -> participantRepository.save((Participant) event.getBean()));
        addComponent(sendersGridFragment);
    }

    private Collection getSenders() {
        return StreamSupport.stream(participantRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    private void setupGridOrdering(Grid grid) {
        //TODO remove ID from grid (last column)
        grid.setColumnOrder("identifier", "name", "outboundEmails", "inboundEmails", "contactPerson", "responsiblePerson");
        //TODO need to add actions here as the last column
    }
}
