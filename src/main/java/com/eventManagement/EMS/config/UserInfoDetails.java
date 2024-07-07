package com.eventManagement.EMS.config;

import com.eventManagement.EMS.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserInfoDetails implements UserDetails {

   private final User user;

    public UserInfoDetails(User user){
       this.user = user;
    }

    public User getUser() {
        return user;
    }
    public Long getId(){
        return user.getUserID();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole()));
    }

    public String getPassword(){
        return user.getPassword();
    }

    public String getUsername(){
        return user.getUsername();
    }


    public String getRole(){
        return user.getRole();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
