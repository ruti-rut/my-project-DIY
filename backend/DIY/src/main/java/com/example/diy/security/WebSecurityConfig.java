package com.example.diy.security;

import com.example.diy.security.jwt.AuthEntryPointJwt;
import com.example.diy.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.http.HttpMethod;
import java.util.List;

// הגדרות אבטחה
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    // השדה userDetailsService הוסר כדי למנוע את שגיאת ה-Bean

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    private AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    /**
     * מגדיר את ספק האימות הראשי (Local Login)
     * מקבל את CustomUserDetailsService ישירות כפרמטר כדי לפתור את שגיאת 'UserDetailsService must be set'
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() { // [1] שונה - אין פרמטר
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // משתמש במשתנה המחלקה המוזרק
        authProvider.setUserDetailsService(customUserDetailsService);
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

    /**
     * מגדירה את שרשרת מסנן האבטחה
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable()).cors(cors->cors.configurationSource(request -> {
                    CorsConfiguration corsConfiguration=new CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200"));
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }))

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/h2-console/**", "/oauth2/**", "/login", "/favicon.ico", "/error").permitAll()
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/category/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/project/**").permitAll()

                                // 2. פרויקטים (POST, PATCH, PUT, DELETE) - דורשים אימות
                                // 🌟 זו הדרך הנכונה לתקן את שגיאת ה-401 שלך:
                                .requestMatchers(HttpMethod.POST, "/api/project/**").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/api/project/**").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/api/project/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/project/**").authenticated()                                .requestMatchers("/api/comment/**").permitAll()
                                .requestMatchers("/api/users/**").permitAll()
                                .requestMatchers("/api/AIAssistant/**").permitAll() // <--- הוספת שורה זו
                                .requestMatchers("/images/**").permitAll() // <--- הוספת השורה הזו
                                .requestMatchers("/api/challenge/**").permitAll()


                                .anyRequest().authenticated()

                );

        http.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler));

        http.headers(headers -> headers.frameOptions(frameOption -> frameOption.sameOrigin()));

        // שימוש במתודת ה-Bean המתוקנת
        // ********* שילוב OAuth2 (כניסה דרך גוגל) *********
        http.oauth2Login(oauth2 -> oauth2
                .loginPage("http://localhost:4200/sign-in") // <-- כאן השם של הדף שלך ב-frontend
                .authorizationEndpoint(endpoint -> endpoint.baseUri("/oauth2/authorization"))
                .redirectionEndpoint(endpoint -> endpoint.baseUri("/oauth2/callback/*"))
                // חיבור ה-Handler המטפל ב-JWT
                .successHandler(oauth2AuthenticationSuccessHandler)
        );
        // ********* סוף OAuth2 *********


        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}