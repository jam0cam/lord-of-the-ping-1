package com.lotp.server.security;

import com.lotp.server.controller.Util;
import com.lotp.server.entity.Player;
import com.lotp.server.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;

/**
 * User: jitse
 * Date: 12/4/13
 * Time: 11:51 AM
 */
@Component
public class MyAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    PlayerRepository playerRepository;

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

        Player user = null;
        try {
            user = playerRepository.findByEmailAndPassword(email, Util.md5(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

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
