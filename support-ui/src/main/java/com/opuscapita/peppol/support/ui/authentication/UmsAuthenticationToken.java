package com.opuscapita.peppol.support.ui.authentication;

import com.itella.sp.usermanagement.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 14.20.1
 * Time: 14:20
 * To change this template use File | Settings | File Templates.
 */
public class UmsAuthenticationToken extends AbstractAuthenticationToken {
    private User user;

    public UmsAuthenticationToken(User user, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        //this.roles = UmsAuthority.convertRoles(user.getRoles());
        this.user = user;
        super.setAuthenticated(true); // must use super, as we override
    }

    // Don't store user credentials..
    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public String getName() {
        return user.getFirstName() + " " + user.getLastName();
    }
}
