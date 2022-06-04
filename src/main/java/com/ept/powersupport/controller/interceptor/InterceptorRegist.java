package com.ept.powersupport.controller.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorRegist implements WebMvcConfigurer {

    @Autowired
    private BusinessInterceptor businessInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(businessInterceptor).addPathPatterns("/business/mece/**");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
