package com.nithin.razorpay.merchant.services;

import com.nithin.razorpay.merchant.dto.request.LoginRequest;
import com.nithin.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.nithin.razorpay.merchant.dto.response.LoginResponse;
import com.nithin.razorpay.merchant.dto.response.MerchantResponse;
import jakarta.validation.Valid;

public interface AuthService {

    MerchantResponse create(MerchantSignupRequest merchantSignupRequest);

    LoginResponse login(LoginRequest request);
}
