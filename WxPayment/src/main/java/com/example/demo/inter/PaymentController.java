package com.example.demo.inter;

import org.springframework.web.bind.annotation.RestController;

/**
 * @author chei1
 */
@RestController
public interface PaymentController {

    String putPayment(String paySn);
}
