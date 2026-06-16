package com.nithin.razorpay.common.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class RandomizerUtil {

    public static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String randomBase64(int length){

        byte[] buf = new byte[length];
        SECURE_RANDOM.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);

    }
}
