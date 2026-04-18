package com.snookerup.config;

import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for the Spring Web MVC.
 *
 * @author Huw
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // Add the ApplicationConversionService, which includes some default converters, for enums and the like
        ApplicationConversionService.configure(registry);
    }
}