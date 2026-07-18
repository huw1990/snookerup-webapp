package com.snookerup.config;

import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for the Spring Web MVC.
 *
 * @author Huw
 */
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // Add the ApplicationConversionService, which includes some default converters, for enums and the like
        ApplicationConversionService.configure(registry);
    }
}