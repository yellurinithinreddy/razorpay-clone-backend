package com.nithin.razorpay.merchant.security;

import com.nithin.razorpay.common.idempotency.RedisIdempotencyFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private static final String[] JWT_ROUTES = {"/v1/auth/**","/v1/merchants/**","/v1/admin/**","/actuator/**"};
    private static final String[] API_KEY_ROUTES = {"/v1/orders/**","/v1/payments/**","/v1/vault/**" };
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;

    private final RedisIdempotencyFilter redisIdempotencyFilter;

    @Bean
    public SecurityFilterChain jwtChain(HttpSecurity http){
        return http
                .securityMatcher(JWT_ROUTES)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v1/auth/signup","/v1/auth/login", "/webhook/**").permitAll()
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(redisIdempotencyFilter,JwtAuthenticationFilter.class)
                .build();
    }

    @Bean
    public SecurityFilterChain apiKeyChain(HttpSecurity http){
        return http
                .securityMatcher(API_KEY_ROUTES)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(redisIdempotencyFilter, ApiKeyAuthenticationFilter.class)
                .build();
    }



    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(MerchantUserDetailsService userDetailsService, PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }
}
