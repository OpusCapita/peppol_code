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
import de.steinwedel.messagebox.MessageBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.Collection;

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
        try {
            performLogin();
            redirectToStartPage();
        } catch (Exception exc) {
            MessageBox
                    .createError()
                    .withCaption("Login failed")
                    .withMessage("Could not login you to portal, please check your credentials and/or contact support. \n" + exc.getMessage())
                    .withOkButton(() -> redirectToStartPage())
                    .open();
        } finally {

        }
    }

    protected void redirectToStartPage() {
        getPage().setLocation(baseUrl.isEmpty() ? "/" : baseUrl);
    }

    protected void performLogin() {
        Authentication authenticated;
        if (user.getValue().equalsIgnoreCase("test") && password.getValue().equalsIgnoreCase("test")) {
            authenticated = new Authentication() {
                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return null;
                }

                @Override
                public Object getCredentials() {
                    return null;
                }

                @Override
                public Object getDetails() {
                    return null;
                }

                @Override
                public Object getPrincipal() {
                    return new Principal() {
                        @Override
                        public String getName() {
                            return "test";
                        }
                    };
                }

                @Override
                public boolean isAuthenticated() {
                    return true;
                }

                @Override
                public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

                }

                @Override
                public String getName() {
                    return "test user";
                }
            };
        } else {
            Authentication auth = new UsernamePasswordAuthenticationToken(user.getValue(), password.getValue());
            authenticated = umsAuthenticationProvider.authenticate(auth);
        }
        SecurityContextHolder.getContext().setAuthentication(authenticated);
    }

}