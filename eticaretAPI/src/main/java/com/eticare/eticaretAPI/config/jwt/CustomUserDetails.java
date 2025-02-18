package com.eticare.eticaretAPI.config.jwt;

import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;

public class CustomUserDetails implements UserDetails {



    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Date accountLockedUntil; // Kullanıcının kilit süresi

    public CustomUserDetails(String email, String password, Collection<? extends GrantedAuthority> authorities, Date accountLockedUntil) {
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.accountLockedUntil = accountLockedUntil;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
                /*accountLockedUntil == null || accountLockedUntil.before(new Date());*/

    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
