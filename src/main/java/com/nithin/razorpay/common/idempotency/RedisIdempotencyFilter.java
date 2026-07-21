package com.nithin.razorpay.common.idempotency;

import com.nithin.razorpay.common.exceptions.IdempotencyConflictException;
import com.nithin.razorpay.merchant.security.MerchantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisIdempotencyFilter extends OncePerRequestFilter {

    private final IdempotencyStore idempotencyStore;

    private final MerchantContext merchantContext;

    private final String SEPARATOR = "|";

    private final Duration IN_PROGRESS_TTL = Duration.ofSeconds(30);

    private final Duration COMPLETED_TTL = Duration.ofHours(24);

    private final HandlerExceptionResolver handlerExceptionResolver;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String rawKey = request.getHeader("X-Idempotency-Key");

        if(rawKey == null || rawKey.isBlank()){
            filterChain.doFilter(request,response);
            return ;
        }

        UUID merchantId = merchantContext.getMerchantId();

        String key = merchantId != null ? merchantId+":"+rawKey : rawKey;

        boolean claimed = idempotencyStore.setIfAbsent(key,IN_PROGRESS_TTL);

        if(!claimed){
            // another thread has already claimed this key
            Optional<String> stored = idempotencyStore.get(key);

            if(stored.isPresent() && !IdempotencyStore.IN_PROGRESS.equals(stored.get())){
                // it's not in-progress, but coming from the actual value stored in redis
                replay(request,response,stored);
            }
            else{
                // it's in progress by another thread
                var ex = new IdempotencyConflictException("A request with this idempotency key is in progress");
                handlerExceptionResolver.resolveException(request,response,null,ex);

            }
            return ;
        }

        // first time claim
        ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);
        try{
            filterChain.doFilter(request,wrapper);
        }finally{
            int status = wrapper.getStatus();
            byte[] bodyBytes = wrapper.getContentAsByteArray();
            String body = new String(bodyBytes, StandardCharsets.UTF_8);

            if(status < 400 && !body.isBlank()){
                // Success — store the completed response for future replays
                String stored = status + SEPARATOR + body;
                idempotencyStore.store(key,stored,COMPLETED_TTL);
                log.debug("IdempotencyFilter: stored response status={} key={}", status, key);
            }else{
                // Error or empty — delete placeholder so client can retry cleanly
                idempotencyStore.delete(key);
                log.debug("IdempotencyFilter: deleted placeholder after error status={} key={}", status, key);
            }
        }
        // Always flush buffered body to the actual response.
        // If this is skipped the client receives an empty body.
        wrapper.copyBodyToResponse();

    }

    private void replay(HttpServletRequest request, HttpServletResponse response, Optional<String> stored) throws IOException {
        int separatorIndex = stored.get().indexOf(SEPARATOR);

        if(separatorIndex < 1){
            var ex = new IdempotencyConflictException("A request with this idempotency key is in progress");
            handlerExceptionResolver.resolveException(request,response,null,ex);
            return ;
        }

        int status = Integer.parseInt(stored.get().substring(0,separatorIndex));

        String body = stored.get().substring(separatorIndex+1);

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));


    }
}
