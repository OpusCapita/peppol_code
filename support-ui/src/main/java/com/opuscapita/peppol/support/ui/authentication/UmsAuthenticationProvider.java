package com.opuscapita.peppol.support.ui.authentication;


import com.itella.sp.usermanagement.User;
import com.itella.sp.usermanagement.UserManagementException;
import com.itella.sp.usermanagement.UserManagementServiceFactory;
import com.itella.sp.usermanagement.client.BasicUmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 14.20.1
 * Time: 11:10
 * To change this template use File | Settings | File Templates.
 */
@Component
public class UmsAuthenticationProvider implements AuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(UmsAuthenticationProvider.class);
    private BasicUmsService basicUmsService;

    @Value("${ums.application}")
    private String applicationName;
    private String username;

    public UmsAuthenticationProvider() {
        this.basicUmsService = UserManagementServiceFactory.getWebServiceClient();
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        username = authentication.getName();
        String password = authentication.getCredentials().toString();
        logger.info("Authentication attempt for the user: " + username);
        try {
            Authentication auth = parseUmsAuthentication(username, password);
            return auth;
        } catch (UserManagementException e) {
            logger.error("Failed to authenticate user: " + username + " in application: " + applicationName + "." +
                    " With exception: " + e.getMessage());
            throw new UsernameNotFoundException(getErrorFault(e));
        } catch (Exception e) {
            logger.error("Failed to connect to UMS: " + e.getMessage());
            e.printStackTrace();
            throw new UsernameNotFoundException("Failed to connect to UMS server...");
        }

    }

    private String getErrorFault(UserManagementException e) {
        int errorNumber = Integer.parseInt(e.getMessage().split(":")[0]);
        switch (errorNumber) {
            case (UserManagementException.NO_SUCH_USERNAME):
            case (UserManagementException.CUSTOMER_HAS_NO_APPLICATION):
            case (UserManagementException.USER_HAS_NO_ROLE_IN_APPLICATION):
            case (UserManagementException.INVALID_PASSWORD):
            case (UserManagementException.NO_SUCH_SEARCH_ATTRIBUTE):
            case (UserManagementException.NO_SUCH_APPLICATION):
                return "Login failed. Invalid username or password.";
            case (UserManagementException.ACCOUNT_LOCKED):
                return "Your account is locked.";
            case (UserManagementException.ACCOUNT_INACTIVE):
                return "Your account is inactive.";
            case (UserManagementException.ACCESS_FROM_IP_NOT_ALLOWED):
                return "Access from your IP is restricted.";
            case (UserManagementException.UNDEFINED):
            default:
                return "Unknown exception. Please contact Helpdesk";
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private Authentication parseUmsAuthentication(String name, String password) throws UserManagementException {
        User umsUser = basicUmsService.authenticate(name, password, applicationName);

        Authentication authentication = new UmsAuthenticationToken(umsUser, UmsAuthority.convertRoles(umsUser.getRoles()));
        return authentication;
    }
}
