package com.example.diy.security;

import com.example.diy.model.Role;
import com.example.diy.model.Users;
import com.example.diy.service.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UsersRepository userRepository;



    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //לאמת את המשתמש עם המשתמש שנמצא ב-DB
        Users user=userRepository.findByUserName(username);
        if (user==null)
            throw new UsernameNotFoundException("user not found");
        //רשימה של הרשאות
        List<GrantedAuthority> grantedAuthorities=new ArrayList<>();
        for(Role role:user.getRoles())
        {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName().name()));
        }
        return new CustomUserDetails(username,user.getPassword(),grantedAuthorities);//יוצר משתמש עבור האבטחה
    }
}
