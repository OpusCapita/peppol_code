package com.opuscapita.peppol.ui.portal.security.ui;

import com.opuscapita.peppol.ui.portal.security.UmsAuthenticationProvider;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringUI(path = "/login")
@Title("LoginPage")
@Theme("valo")
public class LoginUI extends UI {
    private static final Logger logger = LoggerFactory.getLogger(LoginUI.class);
    private static final String username = "username";
    private static final String passwordValue = "test123";
    TextField user;
    PasswordField password;
    @Autowired
    UmsAuthenticationProvider umsAuthenticationProvider;

    @Value(value = "${peppol.portal.baseUrl:/portal}")
    String baseUrl;

    Button loginButton = new Button("Login", this::loginButtonClick);

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();

        user = new TextField("User:");
        user.setWidth("300px");
        user.setRequiredIndicatorVisible(true);
        user.setPlaceholder("Your username");


        password = new PasswordField("Password:");
        password.setWidth("300px");
        password.setRequiredIndicatorVisible(true);
        password.setValue("");

        VerticalLayout fields = new VerticalLayout(user, password, loginButton);
        fields.setCaption("Please login to access the application");
        fields.setSpacing(true);
        fields.setMargin(new MarginInfo(true, true, true, false));
        fields.setSizeUndefined();

        ShortcutListener usernameShortCutListener = new ShortcutListener("Shortcut Name", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                password.focus();
            }
        };

        ShortcutListener passwordShortCutListener = new ShortcutListener("Shortcut Name", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                loginButtonClick(null);
            }
        };

        user.addFocusListener((FieldEvents.FocusListener) event -> user.addShortcutListener(usernameShortCutListener));
        user.addBlurListener((FieldEvents.BlurListener) event -> user.removeShortcutListener(usernameShortCutListener));

        password.addFocusListener((FieldEvents.FocusListener) event -> password.addShortcutListener(passwordShortCutListener));
        password.addBlurListener((FieldEvents.BlurListener) event -> password.removeShortcutListener(passwordShortCutListener));



        VerticalLayout uiLayout = new VerticalLayout(fields);
        uiLayout.setSizeFull();
        uiLayout.setComponentAlignment(fields, Alignment.MIDDLE_CENTER);
        setFocusedComponent(user);

        setContent(uiLayout);
    }

    public void loginButtonClick(Button.ClickEvent e) {
        //authorize/authenticate user
        //tell spring that my user is authenticated and dispatch to my mainUI
        Authentication auth = new UsernamePasswordAuthenticationToken(user.getValue(), password.getValue());
        Authentication authenticated = umsAuthenticationProvider.authenticate(auth);
        SecurityContextHolder.getContext().setAuthentication(authenticated);
        getPage().setLocation(baseUrl.isEmpty() ? "/" : baseUrl);
    }

}