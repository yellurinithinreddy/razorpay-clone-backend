package com.nithin.razorpay.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class EncryptionConfig {

    @Value("${master.secret-key}")
    private String masterKey;

    @Bean
    public BytesEncryptor masterKeyEncryptor(){
        byte[] masterKeyBytes = Base64.getDecoder().decode(masterKey);
        SecretKeySpec key = new SecretKeySpec(masterKeyBytes,"AES");

        return new AesBytesEncryptor(key, KeyGenerators.secureRandom(12),AesBytesEncryptor.CipherAlgorithm.GCM);
    }
}
