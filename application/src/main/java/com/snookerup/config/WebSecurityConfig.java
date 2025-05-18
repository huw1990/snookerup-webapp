package com.snookerup.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Configuration for Spring Security, including which routes are/aren't authenticated, and details of the OAuth 2.0
 * login mechanism.
 *
 * @author Huw
 */
@Configuration
public class WebSecurityConfig {

    private final LogoutSuccessHandler logoutSuccessHandler;

    public WebSecurityConfig(LogoutSuccessHandler logoutSuccessHandler) {
        this.logoutSuccessHandler = logoutSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(withDefaults())
                .oauth2Login(withDefaults())
                .authorizeHttpRequests(httpRequests -> httpRequests
                        .requestMatchers(EndpointRequest.to(HealthEndpoint.class)).permitAll()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/",
                                "/login*",
                                "/routines",
                                "/routines/**",
                                "/routines-frag",
                                "/register*")
                        .permitAll()
                        .anyRequest().authenticated())
                .logout(logout -> logout.logoutSuccessHandler(logoutSuccessHandler));

        return httpSecurity.build();
    }
}
