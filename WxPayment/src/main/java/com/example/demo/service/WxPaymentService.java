package com.example.demo.service;

import com.example.demo.inter.PaymentController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @author chei1
 */
@Service
public class WxPaymentService {
    @Autowired
    PaymentController paymentController;
    public String unifiedWxPayment(){
        String str="0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<18;i++){
            int number=random.nextInt(10);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public String putPayment(String paySn){
        return paymentController.putPayment(paySn);
    }



}
