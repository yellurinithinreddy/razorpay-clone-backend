package com.nithin.razorpay.merchant.services.impl;

import com.nithin.razorpay.common.exceptions.ResourceNotFoundException;
import com.nithin.razorpay.merchant.entities.Customer;
import com.nithin.razorpay.merchant.entities.Merchant;
import com.nithin.razorpay.merchant.repositories.CustomerRepository;
import com.nithin.razorpay.merchant.repositories.MerchantRepository;
import com.nithin.razorpay.merchant.services.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;


    @Override
    public UUID findOrCreate(UUID merchantId, String email, String name, String phone) {
        if(email == null || email.isBlank()){

            return null;
        }

        return customerRepository.findByMerchant_IdAndEmail(merchantId,email)
                .map(Customer::getId)
                .orElseGet(() -> createNew(merchantId,email,name,phone));
    }

    private UUID createNew(UUID merchantId, String email, String name, String phone) {

        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant",merchantId));

        Customer customer = Customer.builder()
                .email(email)
                .name(name)
                .merchant(merchant)
                .phone(phone)
                .build();

        customer = customerRepository.save(customer);

        log.info("Customer created via findOrCreate id = {} merchantId = {} email= = {} , name = {}",
                customer.getId(),merchantId,email,name);
        return customer.getId();



    }
}
