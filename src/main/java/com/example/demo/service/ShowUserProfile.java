package com.example.demo.service;

import java.util.*;

import com.example.demo.models.Role;
import com.example.demo.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class ShowUserProfile implements UserDetails {
    private User user;

    public ShowUserProfile(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Role> roles = user.getRoles();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }

    public boolean isActive() {
        return user.isActive();
    }

    public Long getId() {
        return user.getId();
    }

    public String getUsername() {
        return user.getUsername();
    }

    public String getPassword() {
        return user.getPassword();
    }

    //-----------------------------------------------------------------------------------

    public String getSurename() {
        return this.user.getSurename();
    }

    public String getName() {
        return user.getName();
    }

    public String getPatron() {
        return user.getPatron();
    }

    public String getAbout_me() {
        return user.getAbout_me();
    }

    public int getAge() {
        return user.getAge();
    }
}
