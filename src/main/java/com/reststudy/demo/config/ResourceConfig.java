package com.reststudy.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceConfig extends ResourceServerConfigurerAdapter {


    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("event");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .anonymous()
        .and()
        .authorizeRequests()
        .mvcMatchers(HttpMethod.GET,"/api/**").anonymous()
                .mvcMatchers(HttpMethod.POST,"/api/**").anonymous()
                .anyRequest().authenticated()
        .and()
        .exceptionHandling()
        .accessDeniedHandler(new OAuth2AccessDeniedHandler());
        //위의 예외가 발생하는 경우는 인증이 잘못된다던가 권한이 없는경우임.
        //이경우 접근 권한이 없는 경우OAuth2AccessDeniedHandler를 발생시킴
    }
}
