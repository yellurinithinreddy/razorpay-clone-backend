package com.nithin.razorpay.vault.services.impl;

import com.nithin.razorpay.common.entities.Money;
import com.nithin.razorpay.common.enums.CardBrand;
import com.nithin.razorpay.common.enums.PaymentMethod;
import com.nithin.razorpay.common.exceptions.ResourceNotFoundException;
import com.nithin.razorpay.common.util.RandomizerUtil;
import com.nithin.razorpay.merchant.repositories.CustomerRepository;
import com.nithin.razorpay.payment.processor.PaymentProcessorRouter;
import com.nithin.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.nithin.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.nithin.razorpay.vault.config.VaultEncryptionConfig;
import com.nithin.razorpay.vault.dto.request.TokenizeRequest;
import com.nithin.razorpay.vault.dto.response.TokenizeResponse;
import com.nithin.razorpay.vault.entities.CardToken;
import com.nithin.razorpay.vault.entities.VaultCard;
import com.nithin.razorpay.vault.repositories.CardTokenRepository;
import com.nithin.razorpay.vault.repositories.VaultCardRepository;
import com.nithin.razorpay.vault.services.VaultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VaultServiceImpl implements VaultService {

    private final VaultCardRepository vaultCardRepository;
    private final CardTokenRepository cardTokenRepository;
    private final BytesEncryptor dekEncryptor;
    private final PaymentProcessorRouter paymentProcessorRouter;

    @Override
    @Transactional
    public TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId) {

        String lastFour = request.pan().substring(request.pan().length()-4);
        String bin = request.pan().substring(0,6);
        CardBrand brand = detectBrand(request.pan());

        byte[] dek = KeyGenerators.secureRandom(32).generateKey();
        byte[] encryptedPan = VaultEncryptionConfig.panEncryptor(dek).encrypt(request.pan().getBytes(StandardCharsets.UTF_8));
        byte[] encryptedDek = dekEncryptor.encrypt(dek);

        VaultCard vaultCard = vaultCardRepository.save(
                VaultCard.builder()
                        .bin(bin)
                        .cardHolderName(request.cardHolderName())
                        .encryptedPan(encryptedPan)
                        .encryptedDek(encryptedDek)
                        .expiryMonth(request.expiryMonth().toString())
                        .expiryYear(request.expiryYear().toString())
                        .lastFour(lastFour)
                        .brand(brand.name())
                        .build()
        );

        String token = "tok_"+ RandomizerUtil.randomBase64(32);

        CardToken cardToken = cardTokenRepository.save(
                CardToken.builder()
                        .merchant(merchantId)
                        .customer(request.customerId())
                        .token(token)
                        .vaultCard(vaultCard)
                        .build()
        );

        return new TokenizeResponse(token,lastFour,brand, request.expiryMonth(), request.expiryYear());
    }

    @Override
    public PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String,Object> methodDetails) {
        CardToken cardToken = cardTokenRepository.findByTokenAndRevokedAtIsNull(token)
                .orElseThrow(() -> new ResourceNotFoundException("Card Token",token));

        VaultCard card = cardToken.getVaultCard();
        byte[] panBytes = null;

        try{

            byte[] dek = dekEncryptor.decrypt(card.getEncryptedDek());
            panBytes = VaultEncryptionConfig.panEncryptor(dek).decrypt(card.getEncryptedPan());
            String pan = new String(panBytes,StandardCharsets.UTF_8);
            String expiry = card.getExpiryMonth()+"/"+card.getExpiryYear();

            PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest.card(pan,expiry,paymentId,PaymentMethod.CARD,amount,methodDetails);

            PaymentProcessorResponse paymentProcessorResponse = paymentProcessorRouter.charge(paymentProcessorRequest);

            log.info("Vault charge registered, token = {}****",token.substring(0,4));

            return paymentProcessorResponse;
        }catch (Exception e){
            log.warn("Vault charge failed, token = {}****",token.substring(0,4));
            return new PaymentProcessorResponse.Failure("VAULT_CHARGE_FAILED",e.getMessage());
        }finally {
            if(panBytes != null) Arrays.fill(panBytes,(byte)0);
        }
    }

    private CardBrand detectBrand(String pan) {
        if(pan.startsWith("4")) return CardBrand.VISA;
        if(pan.startsWith("5") || pan.startsWith("2")) return CardBrand.MASTERCARD;
        if(pan.startsWith("37") || pan.startsWith("34")) return CardBrand.AMEX;
        return CardBrand.RUPAY;
    }
}
