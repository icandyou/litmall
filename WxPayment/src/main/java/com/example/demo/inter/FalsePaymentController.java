package com.example.demo.inter;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class FalsePaymentController implements PaymentController {
    @Override
    public String putPayment(String paySn){
        return "Success";
    }
}
