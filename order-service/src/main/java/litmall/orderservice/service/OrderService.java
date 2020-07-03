package litmall.orderservice.service;


import domain.*;
import litmall.orderservice.controller.otherInterface.*;
import litmall.orderservice.dao.OrderDao;
import litmall.orderservice.util.JacksonUtil;
import litmall.orderservice.util.ResponseUtil;
import litmall.orderservice.mapper.OrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author lsz
 * @create 2019/12/6 20:54
 */
@Service("OrderService")
public class OrderService {

    @Resource
    OrderMapper orderMapper;

    @Autowired
    private OrderDao orderDao;

    @Resource
    CartController cartController;
    @Resource
    DiscountController discountController;
    @Resource
    GoodsController goodsController;
    @Resource
    FrightController frightController;
    @Resource
    PaymentController paymentController;

    private static final Logger logger= LoggerFactory.getLogger(OrderService.class);


    public Object submitOrder(Order order, List<CartItem> cartItems){

        System.out.println(cartItems.size());
        if(this.createOrderItemByCartItem(order,cartItems)){

            logger.debug("order: "+order.getOrderItemList().toString());
            String orderStr= JacksonUtil.toJson(discountController.cabalDiscount(order));
            order=JacksonUtil.parseObject(orderStr,"data",Order.class);

            logger.debug("paymentSize: "+String.valueOf(order.getPaymentList().size()));
            System.out.println(order.getPaymentList().size());

            BigDecimal rebate=order.getRebatePrice();
            order.setDealPriceByRebate();

//            order.setFreightPrice(frightController.getFright(order));
            Object object=frightController.getFright(order);

            order.setPaymentList(0,order.getPaymentList().get(0).getActualPrice().add(order.getFreightPrice()));

            order=orderDao.addOrder(order);


            for(Payment payment:order.getPaymentList()){
                paymentController.createPayment(payment);
            }
            return order;
        }
        return ResponseUtil.fail(602,"订单提交失败");
    }
    public Boolean createOrderItemByCartItem(Order order, List<CartItem> cartItems){
        Boolean ret=true;
        List<OrderItem> orderItems=new ArrayList<>(cartItems.size());
        System.out.println("when create order :");
        for(CartItem cartItem:cartItems) {

            System.out.println(cartItem);
            OrderItem orderItem = new OrderItem(cartItem);
            orderItems.add(orderItem);
        }
        String state=null;
        logger.debug("before decute product:"+ret);
        try{
            state=JacksonUtil.toJson(goodsController.updateDecute(false,orderItems));
        }catch (Exception e){
            e.printStackTrace();
        }
        logger.debug("state");
        ret=JacksonUtil.parseBoolean(state,"data");
        logger.debug("after decute product:"+ret);

        System.out.println(ret);

        if(ret){
            for(CartItem cartItem:cartItems){
                String string=JacksonUtil.toJson(cartController.deleteCart(cartItem.getId()));
                logger.debug("cart"+string);
            }
            order.setOrderItemList(orderItems);
        }
        return ret;
    }




    public List<Order> findOrdersByUserId(Integer userId, Integer page, Integer limit,Integer showType){
        logger.debug("this is in service "+userId+"'s order");

        return orderDao.findOrdersByUserId(userId,page,limit,showType);
    }

    public void addOrder(Order order){
        orderMapper.addOrder(order);
    }


    public Object deleteOrderById(Integer id,Integer userId) {
        Order newOrder=orderMapper.findOrderById(id);
        if(!newOrder.getUserId().equals(userId)){
            return ResponseUtil.illegal();
        }

        boolean state=orderDao.deleteOrderById(id);
        if(state){
            return ResponseUtil.ok();
        }else {
            return ResponseUtil.fail(607,"订单取消失败");
        }
    }


    public Order findOrderDetail(Integer orderId) {
        Order order=orderDao.findOrderDetail(orderId);
        return order;
    }

    public Order orderCancel(Order order){

        if(JacksonUtil.parseBoolean(JacksonUtil.toJson(goodsController.updateDecute(true,order.getOrderItemList())),"data")){
            order=orderDao.updateOrderState(order);
        }
        return order;
    }

    public Object alterOrderStatus(Order order,Integer userId) {
        Order newOrder=orderMapper.findOrderById(order.getId());

        if(newOrder==null){
            return ResponseUtil.fail(600,"该订单不存在");
        }else if(!newOrder.getUserId().equals(userId)){
            System.out.println(userId);
            return ResponseUtil.fail(607,"无法操作不属于自己的订单");
        }else if(newOrder.getStatusCode()>=order.getStatusCode()){
            return ResponseUtil.fail(604,"修改订单状态失败");
        }

        newOrder.setStatusCode(order.getStatusCode());
        order=orderDao.updateOrderState(newOrder);
        return ResponseUtil.ok(order);
    }

//    public Object adminAlterOrder(Order order){
//        Order newOrder=orderMapper.findOrderById(order.getId());
//        if(newOrder==null){
//            return ResponseUtil.notFind();
//        }
//        newOrder.setStatusCode(order.getStatusCode());
//        order=orderDao.updateOrderState(newOrder);
//        return ResponseUtil.ok(order);
//    }

    public OrderItem adminAlterOrderItemStatus(OrderItem orderItem){
        OrderItem newOrderItem=new OrderItem();

        int num=orderMapper.updateOrderItem(orderItem);
        if(num==1){
            return orderItem;
        }else{
            return null;
        }
    }

    public OrderItem alterOrderItemStatus(OrderItem orderItem){
        OrderItem newOrderItem=new OrderItem();

        int num=orderMapper.updateOrderItem(orderItem);
        if(num==1){
            return orderItem;
        }else{
            return null;
        }
    }

    public ArrayList<Order> findOrdersSevenDay() {
        return orderMapper.shareIdSevenDayAgo();
    }

    public ArrayList<Order> findOrdersByGroupRule(GrouponRulePo grouponRulePo){
        LocalDateTime begin=grouponRulePo.getStartTime();
        LocalDateTime end=grouponRulePo.getEndTime();
        Integer goodsId=grouponRulePo.getGoodsId();
        ArrayList<OrderItem> orderItems=orderMapper.findOrderByRule(begin,end,goodsId,2);

        ArrayList<Order> orders=new ArrayList<>(orderItems.size());
        for(OrderItem orderItem:orderItems){
            Order order=orderDao.findOrderDetail(orderItem.getId());
            orders.add(order);
        }

        return orders;
    }

    /**
     * 这个price是负数要记得
     * @param grouponRulePo
     * @param price
     */
    public void grouponRefund(GrouponRulePo grouponRulePo,BigDecimal price) {

        LocalDateTime begin=grouponRulePo.getStartTime();
        LocalDateTime end=grouponRulePo.getEndTime();
        Integer goodsId=grouponRulePo.getGoodsId();
        ArrayList<OrderItem> orderItems=orderMapper.findOrderByRule(begin,end,goodsId,2);

        ArrayList<Order> orders=new ArrayList<>(orderItems.size());
        for(OrderItem orderItem:orderItems){
            orderItem.setDealPrice(orderItem.getDealPrice().multiply(BigDecimal.ONE.subtract(price)));
            Order order=orderDao.findOrderDetail(orderItem.getId());
            order.setIntegralPrice(orderItem.getDealPrice().multiply(new BigDecimal(orderItem.getNumber())));
            orderMapper.updateOrder(order);
            orderMapper.updateOrderItem(orderItem);

            Payment payment=new Payment();
            payment.setActualPrice(price);
            payment.setBeginTime(LocalDateTime.now());
            payment.setEndTime(LocalDateTime.now());
            payment.setStatusCode(1);
            paymentController.createPayment(payment);
        }


    }


    public ArrayList<Order> setPreSaleFund(PresaleRule presaleRule) {

        System.out.println("begin");
        logger.debug("begin");
        LocalDateTime begin=presaleRule.getStartTime();
        LocalDateTime end=presaleRule.getEndTime();
        Integer goodsId=presaleRule.getGoodsId();

        System.out.println(end);
        ArrayList<OrderItem> orderItems=orderMapper.findOrderByRule(begin,end,goodsId,1);

        ArrayList<Order> orders=new ArrayList<>(orderItems.size());
        for(OrderItem orderItem:orderItems){

            Order order=orderDao.findOrderDetail(orderItem.getOrderId());

            Payment payment=new Payment();
            BigDecimal temp=new BigDecimal(-1);
            payment.setActualPrice(temp.multiply(order.getIntegralPrice()));
            payment.setBeginTime(LocalDateTime.now());
            payment.setEndTime(LocalDateTime.now());
            payment.setStatusCode(1);
            payment.setOrderId(order.getId());
            paymentController.createPayment(payment);

            orderItem.setDealPrice(BigDecimal.ZERO);
            orderMapper.updateOrderItem(orderItem);

            order.setIntegralPrice(BigDecimal.ZERO);
            orderMapper.updateOrder(order);
        }

        return orders;

    }

    public Object createPayment(Integer id) {

        if(null==orderMapper.findOrderById(id)){
            return ResponseUtil.notFind();
        }

        ArrayList<Payment> payments=new ArrayList<>();
        Gson gson=new Gson();

        String temp= gson.toJson(paymentController.getPaymentByOrderId(id));
        JsonObject jsonObject=new JsonParser().parse(temp).getAsJsonObject();
        JsonArray array=jsonObject.get("data").getAsJsonArray();
        for(JsonElement jsonElement:array){
            JsonObject jo=jsonElement.getAsJsonObject();
            System.out.println(jo);
            Payment payment=new Payment();
            payment.setPaySn(jo.get("paySn").getAsString());
            payment.setStatusCode(jo.get("statusCode").getAsInt());

            payments.add(payment);
        }


        System.out.println(payments.size());
        for(Payment payment:payments){

            if(payment.getStatusCode()==0){
                logger.debug(payment.getPaySn());
                Object object=paymentController.payPayment(payment.getPaySn());
                logger.debug("success");
                return object;
            }
        }
        return null;
    }

    public Object orderItemexchange(Integer orderItemId) {

        OrderItem orderItem=orderMapper.findOrderItemsById(orderItemId);
        ArrayList<OrderItem> orderItems=new ArrayList<>();
        orderItems.add(orderItem);

        Order order=new Order();
        order.setOrderItemList(orderItems);
        order.setIntegralPrice(BigDecimal.ZERO);

        orderMapper.addOrder(order);
        return ResponseUtil.ok(order);
    }

    public Integer findOrderItemById(Integer id) {

        OrderItem orderItem=orderMapper.findOrderItemsById(id);
        return orderItem.getItemType();

    }

    public boolean finishPay() {
        Order finishOrder=new Order();
        finishOrder.setStatusCode(3);
        finishOrder=orderDao.updateOrderState(finishOrder);
        if(finishOrder!=null){
            return true;
        }
        return false;
    }

    public Object cancelOrder(Order order, Integer userId) {
        this.alterOrderStatus(order,userId);
        Order orderTemp=orderMapper.findOrderById(order.getId());
        goodsController.updateDecute(true,orderTemp.getOrderItemList());

        return ResponseUtil.ok(order);
    }


}
