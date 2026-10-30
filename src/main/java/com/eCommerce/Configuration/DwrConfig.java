package com.eCommerce.Configuration;

import org.directwebremoting.spring.DwrSpringServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DwrConfig {
    @Bean
    public ServletRegistrationBean<DwrSpringServlet> dwrServlet() {
        ServletRegistrationBean<DwrSpringServlet> registrationBean = new ServletRegistrationBean<>();
        registrationBean.setServlet(new DwrSpringServlet());
        registrationBean.addUrlMappings("/dwr/*"); // your preferred mapping
        registrationBean.addInitParameter("debug", "true"); // optional for dev
        return registrationBean;
    }
}
