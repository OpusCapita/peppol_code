package com.opuscapita.peppol.ui.portal.ui.views;

import com.opuscapita.peppol.commons.revised_model.Participant;
import com.opuscapita.peppol.ui.portal.model.ParticipantRepository;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.GridFragmentMode;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.GridFragmentType;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.PlainGridFragment;
import com.opuscapita.peppol.ui.portal.ui.views.util.ViewUtil;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
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
    void init() {
        setSizeFull();
        setDefaultComponentAlignment(Alignment.TOP_LEFT);
        setMargin(false);
        ViewUtil.updateSessionViewObject(httpSession, ViewName.SENDERS);

        PlainGridFragment sendersGridFragment = new PlainGridFragment(GridFragmentType.SENDERS, GridFragmentMode.ALL, Participant.class, getSenders());
        setupGridOrdering(sendersGridFragment.getGrid());
        sendersGridFragment.setCaption("Senders");
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
