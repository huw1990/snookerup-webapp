package com.snookerup.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configures the Spring Web MVC to add our MDC Handler Interceptor, to allow us to intercept all web requests
 * to set the correct MDC value.
 *
 * @author Huw
 */
@Component
class LoggingContextConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingContextInterceptor());
    }
}
