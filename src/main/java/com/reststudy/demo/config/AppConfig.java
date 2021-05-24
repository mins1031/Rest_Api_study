package com.reststudy.demo.config;

import com.reststudy.demo.accounts.Account;
import com.reststudy.demo.accounts.AccountRole;
import com.reststudy.demo.accounts.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

import java.util.Set;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        //새로나온 패스워드 인코더 방식인데 인코딩된 패스워드 앞에 어떤 방식으로 암호화
        //되었는지 주석느낌으로 달아주고 그다음 인코딩된 패스워드가 들어온다고함
    }

    @Bean
    public ApplicationRunner applicationRunner(){
        return new ApplicationRunner() {

            @Autowired
            AccountService accountService;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                Account account = Account.builder()
                        .email("urisegea@naver.com")
                        .password("min")
                        .roles(Set.of(AccountRole.ADMIN,AccountRole.USER))
                        .build();
                accountService.saveAccount(account);
            }
        };
    }
}
