package com.opuscapita.peppol.ui.portal.ui.views;

import com.opuscapita.peppol.commons.revised_model.Customer;
import com.opuscapita.peppol.ui.portal.model.CustomerRepository;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.GridFragmentMode;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.GridFragmentType;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.PlainGridFragment;
import com.opuscapita.peppol.ui.portal.ui.views.util.ViewUtil;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
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
    HttpSession httpSession;

    @Autowired
    CustomerRepository customerRepository;

    @PostConstruct
    void init() {
        setSizeFull();
        setDefaultComponentAlignment(Alignment.TOP_LEFT);
        setMargin(false);
        ViewUtil.updateSessionViewObject(httpSession, ViewName.SENDERS);

        PlainGridFragment sendersGridFragment = new PlainGridFragment(GridFragmentType.SENDERS, GridFragmentMode.ALL, Customer.class, getSenders());
        sendersGridFragment.setCaption("Senders");
        addComponent(sendersGridFragment);
    }

    private Collection getSenders() {
        return StreamSupport.stream(customerRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }
}
