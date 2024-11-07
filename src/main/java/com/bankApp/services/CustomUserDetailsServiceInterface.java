package com.bankApp.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface CustomUserDetailsServiceInterface {

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

}
