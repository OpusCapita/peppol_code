package com.opuscapita.peppol.ui.portal.ui.views;

import com.opuscapita.peppol.commons.model.AccessPoint;
import com.opuscapita.peppol.ui.portal.model.AccessPointRepository;
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

@SpringView(name = ViewName.ACCESS_POINTS)
public class AccessPointsView extends VerticalLayout implements View {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    HttpSession httpSession;

    @Autowired
    AccessPointRepository accessPointRepository;

    @PostConstruct
    void init() {
        setSizeFull();
        setDefaultComponentAlignment(Alignment.TOP_LEFT);
        setMargin(false);
        ViewUtil.updateSessionViewObject(httpSession, ViewName.ACCESS_POINTS);

        PlainGridFragment sendersGridFragment = new PlainGridFragment(GridFragmentType.ACCESS_POINTS, GridFragmentMode.ALL, AccessPoint.class, getAccessPoints());
        sendersGridFragment.setCaption("Access points");
        addComponent(sendersGridFragment);
    }

    private Collection getAccessPoints() {
        return StreamSupport.stream(accessPointRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }
}
