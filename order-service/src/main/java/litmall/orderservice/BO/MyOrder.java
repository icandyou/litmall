package litmall.orderservice.BO;

import domain.Order;
import domain.OrderItem;
import domain.Payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author lsz
 * @create 2020/7/3 9:56
 */
public class MyOrder {

    Order order;

    public MyOrder(Order order) {
        this.order = order;
    }

    public void setDealPriceByRebate(){

        Integer num=order.getOrderItemList().size();
        ArrayList<OrderItem> orderItems=new ArrayList<>(num);
        BigDecimal average=order.getRebatePrice().divide(BigDecimal.valueOf(num),2, RoundingMode.UP);
        for(int i=0;i<num;i++){
            orderItems.get(i).setDealPrice(orderItems.get(i).getDealPrice().subtract(average));
        }
        BigDecimal total=BigDecimal.ZERO;
        for(int i=0;i<num;i++){
            total.add(orderItems.get(i).getDealPrice());
        }

        BigDecimal differ=order.getRebatePrice().subtract(average.multiply(BigDecimal.valueOf(num)));
        orderItems.get(0).setDealPrice(orderItems.get(0).getDealPrice().add(differ));

        order.setOrderItemList(orderItems);
    }

    public void setIntegralPrice(){
        BigDecimal total=BigDecimal.ZERO;
        for(OrderItem orderItem:order.getOrderItemList()){
            total.add(orderItem.getDealPrice());
        }
        total.add(order.getFreightPrice());
    }

    public void setPaymentList(int index,BigDecimal money){
        List<Payment> payments=order.getPaymentList();
        payments.get(0).setActualPrice(money);
        order.setPaymentList(payments);
    }

}
