package com.opuscapita.peppol.ui.portal.ui.views;

import com.opuscapita.peppol.ui.portal.ui.views.fragments.GridFragment;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.GridFragmentMode;
import com.opuscapita.peppol.ui.portal.ui.views.fragments.GridFragmentType;
import com.opuscapita.peppol.ui.portal.ui.views.util.ViewUtil;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

@SpringView(name = ViewName.ACCESS_POINTS)
public class AccessPointsView extends VerticalLayout implements View {
    @Autowired
    HttpSession httpSession;

    @PostConstruct
    void init() {
        setSizeFull();
        setDefaultComponentAlignment(Alignment.TOP_LEFT);
        setMargin(false);
        ViewUtil.updateSessionViewObject(httpSession,ViewName.ACCESS_POINTS);

        GridFragment sendersGridFragment = new GridFragment(GridFragmentType.ACCESS_POINTS, GridFragmentMode.ALL);
        sendersGridFragment.setCaption("Access points");
        addComponent(sendersGridFragment);
    }
}