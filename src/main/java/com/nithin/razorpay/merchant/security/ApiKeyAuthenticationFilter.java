package com.nithin.razorpay.merchant.security;

import com.nithin.razorpay.common.exceptions.RateLimitException;
import com.nithin.razorpay.common.ratelimit.RateLimitResult;
import com.nithin.razorpay.common.ratelimit.RateLimiter;
import com.nithin.razorpay.merchant.cache.ApiKeyCache;
import com.nithin.razorpay.merchant.cache.ApiKeyCacheEntry;
import com.nithin.razorpay.merchant.entities.ApiKey;
import com.nithin.razorpay.merchant.repositories.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
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
@Slf4j
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private String BASIC_PREFIX = "Basic ";

    private final ApiKeyRepository apiKeyRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final MerchantContext merchantContext;

    private final HandlerExceptionResolver handlerExceptionResolver;

    private final ApiKeyCache apiKeyCache;

    private final RateLimiter rateLimiter;

    @Value("${app.rate-limit.use-case.api-key.requests-per-minute}")
    private int maxRequestsPerMinute;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Incoming Request: {}",request.getRequestURI());
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


            ApiKeyCacheEntry apiKeyEntry = apiKeyCache.get(keyId).orElseGet(() -> loadCache(keyId));




            if(apiKeyEntry == null || !apiKeyEntry.enabled() || !secretMatches(apiKeyEntry,rawSecret)){
                throw new BadRequestException("apiKey is disabled or secret didn't match");
            }

            RateLimitResult rateLimitResult = rateLimiter.check("api-key:"+keyId,maxRequestsPerMinute,60);

            if(!rateLimitResult.isAllowed()){
                log.warn("Too many requests keyId: {}",keyId);
                throw new RateLimitException("Too many Requests",rateLimitResult.retryAfterSeconds());
            }

            response.setHeader("X-RateLimit-Limit",String.valueOf(maxRequestsPerMinute));
            response.setHeader("X-RateLimit-Remaining",String.valueOf(rateLimitResult.remaining()));

            var authToken = new UsernamePasswordAuthenticationToken(keyId,null,
                    List.of(new SimpleGrantedAuthority("API_KEY_ROLE")));

            SecurityContextHolder.getContext().setAuthentication(authToken);

            merchantContext.setMerchantId(apiKeyEntry.merchantId());
            merchantContext.setKeyId(apiKeyEntry.keyId());

            filterChain.doFilter(request,response);
        }catch(Exception ex){
            handlerExceptionResolver.resolveException(request,response,null,ex);
        }

    }

    private ApiKeyCacheEntry loadCache(String keyId) {
        ApiKey apiKey = apiKeyRepository.findByKeyId(keyId)
                .orElse(null);

        if(apiKey == null) return null;
        ApiKeyCacheEntry apiKeyCacheEntry = new ApiKeyCacheEntry(keyId,apiKey.getKeySecretHash(),apiKey.getPreviousKeySecretHash(),apiKey.getGracePeriodExpiresAt(),apiKey.getMerchant().getId(),apiKey.getEnvironment(), apiKey.isEnabled());
        apiKeyCache.put(keyId,apiKeyCacheEntry);
        return apiKeyCacheEntry;
    }

    private boolean secretMatches(ApiKeyCacheEntry apiKeyCacheEntry,String rawSecret){
        if(passwordEncoder.matches(rawSecret,apiKeyCacheEntry.keySecretHash())){
            return true;
        }
        return apiKeyCacheEntry.inGracePeriod() &&
                apiKeyCacheEntry.previousKeySecretHash() != null
                && passwordEncoder.matches(rawSecret,apiKeyCacheEntry.previousKeySecretHash());
    }

    private String[] decode(String header) {
        String encoded = header.substring(BASIC_PREFIX.length());
        String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);

        int colon = decoded.indexOf(":");
        if(colon < 1) return null;

        return new String[]{decoded.substring(0,colon),decoded.substring(colon+1)};
    }
}
