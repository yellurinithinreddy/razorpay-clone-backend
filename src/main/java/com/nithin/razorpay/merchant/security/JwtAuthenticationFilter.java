package com.nithin.razorpay.merchant.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MerchantContext merchantContext;
    private final HandlerExceptionResolver handlerExceptionResolver;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Incoming Request: {}",request.getRequestURI());

        try{

            String authenticationHeader = request.getHeader("Authorization");

            if(authenticationHeader == null || !authenticationHeader.startsWith("Bearer ")){
                filterChain.doFilter(request,response);
                return ;
            }

            String accessToken = authenticationHeader.substring("Bearer ".length());

            Claims claims = jwtUtil.verifyAccessToken(accessToken);

            if(claims != null && SecurityContextHolder.getContext().getAuthentication() == null){
                var authToken = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,List.of(new SimpleGrantedAuthority("ROLE_"+jwtUtil.extractRole(claims))));

                SecurityContextHolder.getContext().setAuthentication(authToken);
                merchantContext.setMerchantId(UUID.fromString(jwtUtil.extractMerchantId(claims)));
            }

            filterChain.doFilter(request,response);
        }catch(Exception ex){
            handlerExceptionResolver.resolveException(request,response,null,ex);
        }

    }
}
