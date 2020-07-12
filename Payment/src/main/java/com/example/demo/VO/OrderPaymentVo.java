package com.example.demo.VO;

import java.math.BigDecimal;

/**
 * @author chei1
 */
public class OrderPaymentVo{

    private Integer orderId;
    private  Integer payChannel;
    private BigDecimal actualPrice;

    @Override
    public String toString() {
        return "OrderPaymentVo{" +
                "orderId=" + orderId +
                ", payChannel=" + payChannel +
                ", actualPrice=" + actualPrice +
                '}';
    }



    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(Integer payChannel) {
        this.payChannel = payChannel;
    }

    public BigDecimal getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(BigDecimal actualPrice) {
        this.actualPrice = actualPrice;
    }
}