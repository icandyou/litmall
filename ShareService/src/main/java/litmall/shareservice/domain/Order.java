package litmall.shareservice.domain;

import domain.Address;
import domain.OrderItem;
import domain.Payment;
import domain.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 数据库与对象模型标准组
 * @Description:订单对象
 * @Data:Created in 14:50 2019/12/11
 **/
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class Order extends OrderPo {
    private Address addressObj;
    private User user;
    private List<OrderItem> orderItemList;
    private Integer couponId;
    private List<Payment> paymentList;


    public void setDealPriceByRebate(){

        Integer num=this.getOrderItemList().size();
        ArrayList<OrderItem> orderItems=new ArrayList<>(num);
        BigDecimal average=this.getRebatePrice().divide(BigDecimal.valueOf(num),2, RoundingMode.UP);
        for(int i=0;i<num;i++){
            orderItems.get(i).setDealPrice(orderItems.get(i).getDealPrice().subtract(average));
        }
        BigDecimal total=BigDecimal.ZERO;
        for(int i=0;i<num;i++){
            total.add(orderItems.get(i).getDealPrice());
        }

        BigDecimal differ=this.getRebatePrice().subtract(average.multiply(BigDecimal.valueOf(num)));
        orderItems.get(0).setDealPrice(orderItems.get(0).getDealPrice().add(differ));

        this.setOrderItemList(orderItems);
    }

    public void setIntegralPrice(){
        BigDecimal total=BigDecimal.ZERO;
        for(OrderItem orderItem:this.getOrderItemList()){
            total.add(orderItem.getDealPrice());
        }
        total.add(this.getFreightPrice());
    }

    public List<Payment> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<Payment> paymentList) {
        this.paymentList = paymentList;
    }

    public void setPaymentList(int index,BigDecimal money){
        List<Payment> payments=this.getPaymentList();
        payments.get(0).setActualPrice(money);
        this.setPaymentList(payments);
    }
}
