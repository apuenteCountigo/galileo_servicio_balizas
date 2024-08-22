package com.galileo.cu.serviciobalizas.interceptores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.MappedInterceptor;

@Component
public class WebMvcConfig {

	@Autowired
	BalizasInterceptor balizasInterceptor;

	@Bean
    public MappedInterceptor balizasIntercept() {
        return new MappedInterceptor(new String[]{"/**"}, balizasInterceptor);
    }
}
