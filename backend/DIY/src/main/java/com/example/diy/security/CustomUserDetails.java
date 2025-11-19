package com.example.diy.security;

import com.example.diy.model.Role;
import com.example.diy.model.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails extends User {

    private final Users user;


    public CustomUserDetails(Users user) {
        super(getPrincipalName(user),
                user.getPassword() != null ? user.getPassword() : "",
                buildAuthorities(user));

        this.user = user;
    }

    private static String getPrincipalName(Users user) {
        if (user.getUserName() != null && !user.getUserName().trim().isEmpty()) {
            return user.getUserName();
        }
        return user.getMail();
    }


    private static Collection<? extends GrantedAuthority> buildAuthorities(Users user) {

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName().name()));
        }
        return authorities;
    }

    public Users getUser() {
        return user;
    }
    public Long getId() {
        return user.getId();
    }
}