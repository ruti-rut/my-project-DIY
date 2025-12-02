package com.example.diy.security.jwt;

import com.example.diy.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//********תפקיד המחלקה:
//
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private CustomUserDetailsService userDetailsService;


    //********תפקיד הפונקציה:
    //מה הפונקציה מקבלת?
    //
//    @Override
//    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
//        try{
//            String jwt=jwtUtils.getJwtFromCookies(httpServletRequest);
//            if (jwt != null) {
//
//                System.out.println("--- JWT Token Found: " + jwt.substring(0, 10) + "..."); // מדפיס רק את ההתחלה
//            } else {
//                System.out.println("--- JWT Token NOT Found in Cookies.");
//            }
//
//            if(jwt !=null && jwtUtils.validateJwtToken(jwt)){
//                System.out.println("--- JWT Token IS VALID for user: " + jwtUtils.getUserNameFromJwtToken(jwt));
//                String userName=jwtUtils.getUserNameFromJwtToken(jwt);
//                UserDetails userDetails= userDetailsService.loadUserByUsername(userName);
//
//                UsernamePasswordAuthenticationToken authentication=
//                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
//
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            }
//        else if (jwt != null) {
//            System.out.println("--- JWT Token FAILED VALIDATION or is null.");
//        }
//
//        }
//        catch (Exception e)
//        {
//            System.out.println(e);
//        }
//        //***************מה משמעות ה-filter??
//        filterChain.doFilter(httpServletRequest,httpServletResponse);
//    }
//

    //
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try{
            String jwt = null;

            // 1. נסה קודם מ-Cookie (כמו שהיה לך)
            jwt = jwtUtils.getJwtFromCookies(httpServletRequest);

            // 2. אם אין Cookie, חפש ב-Authorization Header
            if (jwt == null) {
                String bearerToken = httpServletRequest.getHeader("Authorization");
                if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                    jwt = bearerToken.substring(7);
                    System.out.println("--- JWT Token Found in Authorization Header");
                }
            }

            if (jwt != null) {
                System.out.println("--- JWT Token Found: " + jwt.substring(0, 10) + "...");
            } else {
                System.out.println("--- JWT Token NOT Found.");
            }

            if(jwt != null && jwtUtils.validateJwtToken(jwt)){
                System.out.println("--- JWT Token IS VALID for user: " + jwtUtils.getUserNameFromJwtToken(jwt));
                String userName = jwtUtils.getUserNameFromJwtToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } else if (jwt != null) {
                System.out.println("--- JWT Token FAILED VALIDATION or is null.");
            }

        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        //***************מה משמעות ה-filter??
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }


}