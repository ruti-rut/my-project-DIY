package com.example.diy.security; // *** ודאי שהנתיב הזה נכון לפרויקט שלך ***




import com.example.diy.security.jwt.AuthEntryPointJwt;
import com.example.diy.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

//הגדרות אבטחה
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Qualifier("customUserDetailsService")
    CustomUserDetailsService userDetailsService;
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;


    public WebSecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }
    //********תפקיד הפונקציה:
    //מה הפונקציה מחזירה?
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    //********תפקיד הפונקציה:
    //מגדירה את שרשרת מסנן האבטחה
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //משבית את הגנת CSRF על ידי הפעלת שיטת `csrf()` והשבתתה
        http.csrf(csrf -> csrf.disable()).cors(cors->cors.configurationSource(request -> {
                    CorsConfiguration corsConfiguration=new CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200")); // <--- ללא הגרשיים הכפולים בפנים!
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // מומלץ במקום *
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }))

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                                auth.requestMatchers("/h2-console/**").permitAll()
                                        //כאן נעשה אפשור לפונקציות של הכניסה, הרשמה
                                        .requestMatchers("/api/auth/**").permitAll()
                                        //כל שאר הפונקציות ישארו חסומות אך ורק למשתמשים שנכנסו
                                        //אם רוצים אפשר לאפשר פונקציות מסוימות או קונטרולים מסוימים לכל המשתמשים
                                        //לדוג'
                                        .requestMatchers("/api/category/**").permitAll()
                                        .requestMatchers("/api/project/**").permitAll()
                                        .requestMatchers("/api/comment/**").permitAll()







                                        .requestMatchers("/api/challenge/uploadChallenge").hasRole("ADMIN")
                                        .requestMatchers("/error").permitAll()

                                        //.requestMatchers("/api/recipes/delete")
//                  .requestMatchers("/api/user/signIn").permitAll()
                                        .anyRequest().authenticated()
                );

        http .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler));

        // fix H2 database console: Refused to display ' in a frame because it set 'X-Frame-Options' to 'deny'
        http.headers(headers -> headers.frameOptions(frameOption -> frameOption.sameOrigin()));

        http.authenticationProvider(authenticationProvider());


        //***********משמעות הגדרה זו:
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}