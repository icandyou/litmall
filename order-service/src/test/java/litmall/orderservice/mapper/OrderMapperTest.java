package litmall.orderservice.mapper;

import domain.Order;
import domain.OrderItem;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author lsz
 * @create 2020/7/1 23:12
 */
@SpringBootTest
class OrderMapperTest {

    @Resource
    OrderMapper orderMapper;

    @Test
    void findOrdersByUserId() {
        List<Order> orders=orderMapper.findOrdersByUserId(1,1,10,0);
        assertEquals(orders.get(0).getId(),1);
    }

    @Test
    void findOrderItemsByOrderId() {
        List<OrderItem> orderItems=orderMapper.findOrderItemsByOrderId(1);
        assertEquals(orderItems.get(0).getId(),1);
    }

    @Test
    void findOrderById() {
        Order order=orderMapper.findOrderById(1);
        assertEquals(order.getUserId(),1);
    }

    @Test
    void addOrder() {
        Order order=new Order();
        order.setUserId(2);
        Integer num=orderMapper.addOrder(order);
        assertEquals(order.getUserId(),2);
    }

    @Test
    void addOrderItems() {
        List<OrderItem> orderItems=new ArrayList<>();
        OrderItem orderItem=new OrderItem();
        orderItems.add(orderItem);
        Integer num=orderMapper.addOrderItems(orderItems);
        assertEquals(num,1);
    }

    @Test
    void deleteOrderById() {
        Integer num=orderMapper.deleteOrderById(2);
        assertEquals(num,1);
    }

    @Test
    void deleteOrderItemByOrderId() {
        Integer num=orderMapper.deleteOrderItemByOrderId(3);
        assertEquals(num,1);
    }

    @Test
    void updateOrderByShip() {
        Integer num=orderMapper.updateOrderByShip(1,"nn","nn");
        assertEquals(num,1);
    }

    @Test
    void updateOrder() {
        Order order=orderMapper.findOrderById(1);
        order.setUserId(3);
        Integer num=orderMapper.updateOrder(order);
        assertEquals(num,1);
    }

    @Test
    void updateOrderItem() {
        OrderItem orderItem=orderMapper.findOrderItemsById(1);
        orderItem.setItemType(2);
        Integer num=orderMapper.updateOrderItem(orderItem);
        assertEquals(num,1);
    }

    @Test
    void shareIdSevenDayAgo() {
    }

    @Test
    void findOrderByRule() {
    }

    @Test
    void findOrderItemsById() {
        OrderItem orderItem=orderMapper.findOrderItemsById(1);
        assertEquals(orderItem.getId(),1);
    }
}