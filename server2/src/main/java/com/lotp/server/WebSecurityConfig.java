package com.lotp.server;

import com.lotp.server.security.MyAuthenticationProvider;
import com.lotp.server.security.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

/**
 * User: mruno
 * Date: 5/3/14
 * Time: 8:53 PM
 */
@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    MyUserDetailsService userDetailsService;

    @Autowired
    MyAuthenticationProvider authenticationProvider;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
           .ignoring()
                .antMatchers("/", "/profile**")
                .regexMatchers("/.*service.*");

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/match", "/inbox", "/profile/myProfile")
                .hasRole("USER");
        http
            .formLogin()
                .loginPage("/signin")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .failureUrl("/signin/failedLogin?error")
                .defaultSuccessUrl("/")
                .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/signin/logoutSuccess")
                .permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .authenticationProvider(authenticationProvider)
            .userDetailsService(userDetailsService);
    }
}
