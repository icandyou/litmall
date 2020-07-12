package com.example.demo.controller;


import com.example.demo.inter.PaymentController;
import com.example.demo.service.WxPaymentService;
import com.example.demo.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * @author chei1
 */
@RestController()
public class WxPaymentController {

    @Autowired
    WxPaymentService wxPaymentService;

    @PostMapping("wxpayment")
    public Object unifiedWxPayment() {
        String paySn=wxPaymentService.unifiedWxPayment();
        Object retObj = ResponseUtil.ok(paySn);
        System.out.println("submit的返回值："+retObj);
        return retObj;
    }

    @PutMapping("wxpayment")
    public Object requestWxPayment(String paySn){

        String success= wxPaymentService.putPayment(paySn);
        Object retObj = ResponseUtil.ok(success);
        System.out.println("submit的返回值："+retObj);
        return retObj;
    }
}
