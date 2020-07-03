package litmall.orderservice.dao;

import domain.Order;
import domain.OrderItem;
import litmall.orderservice.controller.otherInterface.GoodsController;
import litmall.orderservice.controller.otherInterface.UserController;
import litmall.orderservice.mapper.OrderMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author lsz
 * @create 2019/12/7 20:06
 */
@Repository
public class OrderDao {

    static final Logger logger = LoggerFactory.getLogger(OrderDao.class);

    @Resource
    OrderMapper orderMapper;

    @Resource
    GoodsController goodsController;
    @Resource
    UserController userController;

    public List<Order> findOrdersByUserId(Integer userId, Integer page, Integer limit, Integer showType) {

        logger.debug("\nthis is in Dao\n");

        List<Order> orders = orderMapper.findOrdersByUserId(userId,(page-1)*limit+1,page*limit+1,showType);

        if(orders.size()>limit){
            orders=orders.subList((page-1)*limit+1,page*limit+1);
        }

        logger.debug(String.valueOf(orders.size()));

        for (Order order : orders) {
            ArrayList<OrderItem> orderItems = orderMapper.findOrderItemsByOrderId(order.getId());

            if(order.getId()==1797){
                logger.debug(order.getOrderSn());
            }
            if(order.getId()==1812){
                logger.debug(order.getOrderSn());
            }
            if(order.getId()==1819){
                logger.debug(order.getOrderSn());
            }
            for (OrderItem orderItem : orderItems) {
                orderItem.setProduct(goodsController.getProductById(orderItem.getProductId()));
            }
            order.setOrderItemList(orderItems);
            order.setUser(userController.findUserById(order.getUserId()));
        }
        return orders;

    }





    public boolean deleteOrderById(Integer id){
        int num=0;
        num=orderMapper.deleteOrderById(id);
        logger.debug(String.valueOf(num));
        num=orderMapper.deleteOrderItemByOrderId(id);
        logger.debug(String.valueOf(num));

        return true;
    }

    /**
     *      * 订单状态
     *      * 0：订单生成,未支付
     *      * 1：下单后未支付，用户取消
     *      * 2：下单后未支付超时系统自动取消
     *      * 3：支付完成，商家未发货
     *      * 4：订单产生，已付款未发货，此时用户取消订单并取得退款（在发货前只要用户点取消订单，无需经过审核）
     *      * 5:商家发货，用户未确认
     *      * 6:用户确认收货
     *      * 7:用户没有确认收货超过一定时间，系统自动确认收货
     *      * 8:已评价
     *
     *      订单项状态，0未付款，1未发货，2未收货，3未评价，4已完成订单(无售后或售后拒绝)，5申请退货，6退货成功，7申请换货，8换货成功，9待付尾款
     * 修改整个订单的状态
     * @param order
     */
    public Order updateOrderState(Order order){
        orderMapper.updateOrder(order);
        ArrayList<OrderItem> orderItems=orderMapper.findOrderItemsByOrderId(order.getId());
        int state=-1;
        switch (order.getStatusCode()){
            case 3 :
                state=1;
                break;
            case 4 :
                state=6;break;
            case 5 :
                state=2;break;
            case 6 :
            case 7 :
                state=3;break;
            case 8 :
                state=4;break;
            default:
                break;
        }
        for(OrderItem orderItem:orderItems){
            if(state!=-1){
                orderItem.setStatusCode(state);
            }
            orderMapper.updateOrderItem(orderItem);
        }
        return order;
    }


    public Order findOrderDetail(Integer orderId) {
        Order order=orderMapper.findOrderById(orderId);
        if(order==null){
            return null;
        }
        ArrayList<OrderItem> orderItems = orderMapper.findOrderItemsByOrderId(order.getId());
        for (OrderItem orderItem : orderItems) {
            orderItem.setProduct(goodsController.getProductById(orderItem.getProductId()));

        }
        order.setOrderItemList(orderItems);
        order.setUser(userController.findUserById(order.getUserId()));
        return order;
    }

    public Order addOrder(Order order) {

        orderMapper.addOrder(order);
        orderMapper.addOrderItems(order.getOrderItemList());
        return order;
    }
}
