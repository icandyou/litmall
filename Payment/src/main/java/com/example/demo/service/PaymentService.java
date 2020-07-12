package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.domain.OrderPo;
import com.example.demo.domain.Payment;
import com.example.demo.inter.WxPaymentController;
import com.example.demo.mapper.PaymentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

import java.time.LocalDateTime;


/**
 * @author chei1
 */
@Service

public class PaymentService {
    @Autowired
    RocketMQProvider rocketMQProvider;
    @Autowired
    WxPaymentController wxPaymentController;
    @Autowired
    PaymentMapper paymentMapper;

    public OrderPo addPayment(OrderPo orderPo){
        Integer orderId=orderPo.getId();
        Integer payChannel=1;
        BigDecimal actualPrice=orderPo.getIntegralPrice();

        //获取当前的日期
        LocalDateTime beginTime = LocalDateTime.now();
        LocalDateTime endTime = beginTime.plusHours(2L);
        System.out.println("支付开始时间"+beginTime);
        System.out.println("支付结束时间"+endTime);
        String paySn=wxPaymentController.unifiedWxPayment();
        System.out.println("付款编号"+paySn);

        Payment payments=new Payment();
        payments.setActualPrice(actualPrice);
        payments.setBeginTime(beginTime);
        payments.setEndTime(endTime);
        payments.setOrderId(orderId);
        payments.setPayChannel(payChannel);
        payments.setPaySn(paySn);

        orderPo.setOrderSn("0");
        orderPo.setEndTime(endTime);

        
        paymentMapper.Create(payments);
        return orderPo;

//        JSONObject jsonObject = new JSONObject();
//        JSONObject jsonObjectPaymentInfo = new JSONObject();
//        jsonObjectPaymentInfo.put("orderId",orderId);
//        jsonObjectPaymentInfo.put("payChannel",payChannel);
//        jsonObjectPaymentInfo.put("beginTime",beginTime);
//        jsonObjectPaymentInfo.put("endTime",endTime);
//        System.out.println(jsonObjectPaymentInfo);


    }
    public String putPayment(String paySn){
        Payment payment = new Payment();
        LocalDateTime payTime = LocalDateTime.now();
        payment.setPayTime(payTime);
        payment.setPaySn(paySn);
        rocketMQProvider.updateMQProducer(payment);
        return "Payment success";
    }

}
