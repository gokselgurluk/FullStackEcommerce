package com.eticare.eticaretAPI.config.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    // Constructor
    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;  // Kullanıcının yetkilerini döndürür
    }

    @Override
    public String getPassword() {
        return password;  // Kullanıcının şifresini döndürür
    }

    @Override
    public String getUsername() {
        return username;  // Kullanıcının adını döndürür
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // Hesap süresinin dolup dolmadığını kontrol eder, burada her zaman true dönebiliriz
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // Hesap kilitlenip kilitlenmediğini kontrol eder, burada her zaman true dönebiliriz
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // Kimlik doğrulama bilgileri (şifre vb.) süresi dolmuş mu kontrol eder
    }

    @Override
    public boolean isEnabled() {
        return true;  // Kullanıcının aktif olup olmadığını kontrol eder
    }
}