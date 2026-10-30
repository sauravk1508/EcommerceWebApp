package com.eCommerce.Configuration;

import org.directwebremoting.spring.DwrAnnotationPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DwrAnnotationConfig {

    @Bean
    public DwrAnnotationPostProcessor dwrAnnotationPostProcessor() {
        return new DwrAnnotationPostProcessor();
    }
}
