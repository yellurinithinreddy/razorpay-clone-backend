package com.nithin.razorpay.merchant.security;

import com.nithin.razorpay.common.exceptions.ResourceNotFoundException;
import com.nithin.razorpay.merchant.entities.AppUser;
import com.nithin.razorpay.merchant.repositories.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MerchantUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email).
        orElseThrow(() ->new ResourceNotFoundException("AppUser",email));
    }
}
