package com.eventManagement.EMS.config;

import com.eventManagement.EMS.models.User;
import com.eventManagement.EMS.repository.UserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;

import java.util.Collection;
import java.util.List;


@Service
public class UserInfoUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserInfoUserDetailsService.class);
    @Autowired
    UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        logger.info("User authenticated: {}", user.getUsername());
        return new UserInfoDetails(user);
    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthorities(user));
//    }
//
//    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
//        return List.of(new SimpleGrantedAuthority(user.getRole()));
//    }

}
