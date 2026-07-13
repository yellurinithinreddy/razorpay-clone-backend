package com.nithin.razorpay.merchant.security;

import com.nithin.razorpay.merchant.entities.ApiKey;
import com.nithin.razorpay.merchant.repositories.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private String BASIC_PREFIX = "Basic ";

    private final ApiKeyRepository apiKeyRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final MerchantContext merchantContext;

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{

            String header = request.getHeader("Authorization");

            if(header == null || !header.startsWith(BASIC_PREFIX)){
                filterChain.doFilter(request,response);
                return ;
            }

    //        Authorization : Basic sdjbfsdkjbskfjehiwjefhkwef:ksdjbvsjfkjasvkjfb
    //        Authorization: Basic DSKJFBVSDLKJFBGEKSJFBSLFVKSDBKJVBSDBCVKJDHCBASKJFB
            String[] credentials = decode(header);
            if(credentials == null){
                throw new BadRequestException("Malformed API key Header");
            }

            String keyId = credentials[0];
            String rawSecret = credentials[1];


            ApiKey apiKey = apiKeyRepository.findByKeyId(keyId)
                    .orElseThrow(() -> new BadRequestException("Invalid Or Missing API key"));

            if(!apiKey.isEnabled() || !secretMatches(apiKey,rawSecret)){
                throw new BadRequestException("apiKey is disabled or secret didn't match");
            }

            var authToken = new UsernamePasswordAuthenticationToken(keyId,null,
                    List.of(new SimpleGrantedAuthority("API_KEY_ROLE")));

            SecurityContextHolder.getContext().setAuthentication(authToken);

            merchantContext.setMerchantId(apiKey.getMerchant().getId());
            merchantContext.setKeyId(apiKey.getKeyId());

            filterChain.doFilter(request,response);
        }catch(Exception ex){
            handlerExceptionResolver.resolveException(request,response,null,ex);
        }

    }

    private boolean secretMatches(ApiKey apiKey,String rawSecret){
        if(passwordEncoder.matches(rawSecret,apiKey.getKeySecretHash())){
            return true;
        }

        boolean isInGracePeriod = apiKey.getGracePeriodExpiresAt() != null && LocalDateTime.now().isBefore(apiKey.getGracePeriodExpiresAt());
        return isInGracePeriod &&
                apiKey.getPreviousKeySecretHash() != null
                && passwordEncoder.matches(rawSecret,apiKey.getPreviousKeySecretHash());
    }

    private String[] decode(String header) {
        String encoded = header.substring(BASIC_PREFIX.length());
        String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);

        int colon = decoded.indexOf(":");
        if(colon < 1) return null;

        return new String[]{decoded.substring(0,colon),decoded.substring(colon+1)};
    }
}
