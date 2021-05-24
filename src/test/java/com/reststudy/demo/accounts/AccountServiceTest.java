package com.reststudy.demo.accounts;

import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.regex.Matcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Test
    public void findByUsername(){
        //Given
        String username = "uri@naver.com";
        String password = "min";

        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN,AccountRole.USER))
                .build();

        accountService.saveAccount(account);
        //When
        UserDetailsService userDetailsService =
                (UserDetailsService) accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        //Then
        assertThat(passwordEncoder.matches(password,userDetails.getPassword()));
    }

    @Test
    public void findByUsernameFail(){
        //Given
        String username = "erewfaef.co";
        expectedException.expect(UsernameNotFoundException.class);
        //예상되는 에러를 먼저 적어줌
        expectedException.expectMessage(Matchers.containsString(username));
        //메세지에 username이 들어있는지 확인하는 구문

        //When
        accountService.loadUserByUsername(username);


        /*try {
            accountService.loadUserByUsername(username);
            fail("supported to be failed");
        } catch (UsernameNotFoundException e){
            assertThat(e.getMessage()).containsSequence(username);
            //에러메세지가 username을 담고있는지 확인하는 구문
        }*/
    }
}