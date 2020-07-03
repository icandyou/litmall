package litmall.orderservice.BO;

import domain.CartItem;
import domain.OrderItem;
import lombok.Getter;

/**
 * @Author lsz
 * @create 2020/7/3 10:07
 */
@Getter
public class MyOrderItem {
    OrderItem orderItem;

    public MyOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public MyOrderItem(CartItem cartItem) {
        System.out.println("这里是：  "+cartItem.toString()+"  "+cartItem.getProduct().toString());

        OrderItem orderItem=new OrderItem();
        orderItem.setProduct(cartItem.getProduct());
        orderItem.setProductId(cartItem.getProductId());
        orderItem.setPrice(cartItem.getProduct().getPrice());
        orderItem.setNumber(cartItem.getNumber());
    }
}
