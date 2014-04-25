package com.lotp.security;

import com.lotp.dao.Dao;
import com.lotp.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * User: jitse
 * Date: 12/4/13
 * Time: 12:08 PM
 */
@Component
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    Dao dao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Player user = dao.getByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username/password.");
        }
        Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }
}
