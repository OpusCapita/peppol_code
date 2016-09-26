package com.opuscapita.peppol.support.ui.authentication;

import com.itella.sp.usermanagement.Role;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 14.20.1
 * Time: 14:21
 * To change this template use File | Settings | File Templates.
 */
public class UmsAuthority extends Role implements GrantedAuthority {

    public UmsAuthority(Role role) {
        setAdmin(role.getAdmin());
        setName(role.getName());
        setApplication(role.getApplication());
        setId(role.getId());
    }

    public static Set<UmsAuthority> convertRoles(Set<Role> roles) {
        Set<UmsAuthority> umsAuthorities = new HashSet<UmsAuthority>();
        for (Role role : roles) {
            umsAuthorities.add(new UmsAuthority(role));
        }
        return umsAuthorities;
    }

    @Override
    public String getAuthority() {
        return getName();
    }
}
