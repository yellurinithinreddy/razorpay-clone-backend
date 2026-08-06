package com.nithin.razorpay.operations.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class DummyMerchantController {

    @PostMapping("/success")
    public ResponseEntity<Void> success(@RequestBody Map<String,Object> requestBody){
        return ResponseEntity.noContent().build();
    }
}
