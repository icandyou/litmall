package litmall.orderservice.BO;

import domain.Order;
import domain.OrderItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * @Author lsz
 * @create 2020/7/3 9:56
 */
public class BoOrder extends Order {

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

}
