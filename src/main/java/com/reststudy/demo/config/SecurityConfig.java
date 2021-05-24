package com.reststudy.demo.config;

import com.reststudy.demo.accounts.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

/**
 * 우선 spring security관련 의존성을 pom에 넣어준 순간부터 우리의 모든 테스트가
 * 깨짐. 시큐리티 의존성을 주입하는 순간부터 스프링 부트는 모든 요청에 대한
 * 인증을 요구하기 때문에 그러함.
 * 그래서 시큐리티 설정을 만들어줌.
 * @EnableWebSecurity과 WebSecurityConfigurerAdapter를 상속받으면
 * 스프링 부트가 자동으로 관리하는 내용에 대해서 벗어나 해당 설정 파일을 따르게됨.
 * */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    public TokenStore tokenStore(){
        return new InMemoryTokenStore();
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService)
        .passwordEncoder(passwordEncoder);
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/docs/index.html");
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
        //스프링 부트가 제공하는 정적리소스들에 대한 기본위치들에 시큐리티가 적용안되게 설정
    }//시큐리티 필터를 적용할지 말지 정하는 메서드임

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .anonymous()//익명사용자 허용
                .and()
                .formLogin()//폼인증 사용
                .and()
                .authorizeRequests()
                .mvcMatchers(HttpMethod.GET, "/api/**").authenticated()
                //나는 /api로 시작하는 get요청들은 모두 인증없이 받겠다
                .mvcMatchers(HttpMethod.POST, "/api/**").authenticated()

                .anyRequest().authenticated();
                //나머지는 인증이 필요로 하게 둔다.
    }
}
