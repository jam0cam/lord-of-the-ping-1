package com.lotp.security;

import com.lotp.dao.Dao;
import com.lotp.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * User: jitse
 * Date: 12/4/13
 * Time: 11:51 AM
 */
@Component
public class MyAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    Dao dao;

    @Autowired
    MyUserContext myUserContext;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        String email = token.getName();
        String password = token.getCredentials().toString();

        if (!StringUtils.hasText(email) || !StringUtils.hasText(password)) {
            //missing fields entered.
            throw new BadCredentialsException("Invalid username/password");
        }

        Player user =  dao.getByEmailAndPassword(email, password);

        if(user == null) {
            throw new BadCredentialsException("Invalid username/password");
        }
        myUserContext.setCurrentUser(user);
        Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
        return new UsernamePasswordAuthenticationToken(user, password, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}
