package litmall.orderservice.controller;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import domain.*;
import litmall.orderservice.controller.otherInterface.*;
import litmall.orderservice.service.OrderService;
import litmall.orderservice.util.GetUser;
import litmall.orderservice.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vo.OrderSubmitVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@RestController
@RequestMapping("")
public class OrderController {


    static Logger logger= LoggerFactory.getLogger(OrderController.class);

    @Autowired
    OrderService orderService;

    @Resource
    FrightController frightController;
    @Resource
    CartController cartController;
    @Resource
    UserController userController;
    @Resource
    PaymentController paymentController;
    @Resource
    ShareController shareController;
    @Resource
    GoodsController goodsController;


    /**
     * 根据相应的条件查找order
     * @param request   网络请求信息
     * @param page      页数
     * @param limit     每页数量
     * @param showType  订单类型
     * @return          查询到的订单
     */
    @GetMapping("/orders")
    public Object getOrders(HttpServletRequest request,@RequestParam(defaultValue = "1") Integer page,
                            @RequestParam(defaultValue = "9999") Integer limit,@RequestParam(defaultValue = "6") Integer showType) {
        Integer userId= GetUser.getUserId(request);
        if(userId==null){
            ResponseUtil.unlogin();
        }
        List<Order> orders=orderService.findOrdersByUserId(userId,page,limit,showType);
        Object object= ResponseUtil.ok(orders);
        return object;
    }

    /**
     * 提交订单
     * @param orderSubmitVo 提交订单 vo
     * @param request       网络请求
     * @return      提交的订单结果
     */
    @PostMapping("/orders")
    public Object submit(@RequestBody OrderSubmitVo orderSubmitVo, HttpServletRequest request){

        Integer userId=GetUser.getUserId(request);
        if(userId==null){
            return ResponseUtil.unlogin();
        }
        User user=userController.findUserById(Integer.valueOf(userId));

        Order order=new Order();

        Gson gson=new Gson();
        JsonParser parser=new JsonParser();

        List<CartItem> cartItems=new ArrayList<>(orderSubmitVo.getCartItemIds().size());
        for(Integer cartItemId:orderSubmitVo.getCartItemIds()){
            Object cartItemObject=cartController.getCartById(cartItemId);
            String jsonStr=gson.toJson(cartItemObject);
            JsonObject jsonObject=parser.parse(jsonStr).getAsJsonObject();
            JsonObject jo=jsonObject.get("data").getAsJsonObject();

            System.out.println(jo);
            CartItem cartItem=gson.fromJson(jo,CartItem.class);

            System.out.println(cartItem+cartItem.getId().toString());

            cartItems.add(cartItem);
        }


        order.setUserId(userId);
        order.setRebatePrice(BigDecimal.valueOf(orderSubmitVo.getRebate()));
        order.setAddressObj(orderSubmitVo.getAddress());
        order.setMessage(orderSubmitVo.getMessage());
        order.setCouponId(orderSubmitVo.getCouponId());
        order.setAddress(order.getAddressObj().toString());

        Object result=orderService.submitOrder(order,cartItems);
        return result;
//        return null;
    }


    /**
     * 根据orderId查询属于自己的订单
     * @param id            order的id
     * @param request       网络请求
     * @return              查询到的结果
     */
    @GetMapping("/orders/{id}")
    public Object getOrderDetail(@PathVariable Integer id,HttpServletRequest request) {
        System.out.println(1);
        Integer userId=GetUser.getUserId(request);
        if(userId==null){
            return ResponseUtil.unlogin();
        }
        logger.debug("userId为："+String.valueOf(userId));
        Order order=orderService.findOrderDetail(Integer.valueOf(id));
        if(order==null){
            return ResponseUtil.fail(600,"无此订单");
        }else if(order.getUserId()==null||order.getOrderItemList().size()==0){
            return ResponseUtil.fail(599,"订单异常");
        } else if(!order.getUserId().equals(userId)){
            return ResponseUtil.fail(604,"非法操作，无法查看不属于自己的订单");
        }
        Object object=ResponseUtil.ok(order);
        return object;
    }

    /**
     *取消订单
     * @param request   网络请求
     * @param id        要取消的订单的id
     * @return  Order   订单操作结果
     */
    @PutMapping("/orders/{id}/cancel")
    public Object cancelOrder(@PathVariable(name = "id") Integer id,HttpServletRequest request) {
        Integer userId=GetUser.getUserId(request);
        if(userId==null){
            return ResponseUtil.unlogin();
        }
        Order order=new Order();
        order.setId(id);
        order.setStatusCode(1);

//        Object object=orderService.cancelOrder(order,userId);
        Object object=orderService.alterOrderStatus(order,userId);


        return object;
    }


    /**
     *删除订单
     * @param request   网络请求
     * @param id        要删除的订单的id
     * @return  Order   订单操作结果
     */
    @DeleteMapping("/orders/{id}")
    public Object deleteOrder(@PathVariable Integer id,HttpServletRequest request) {
        Integer userId=GetUser.getUserId(request);
        if(userId==null){
            return ResponseUtil.unlogin();
        }

        Object object=orderService.deleteOrderById(id,userId);
        return object;
    }


    /**
     *确认订单
     * @param request   网络请求
     * @param id        要确认的订单的id
     * @return  Order   订单操作结果
     */
    @PutMapping("/orders/{id}/confirm")
    public Object confirmOrder(@PathVariable Integer id,HttpServletRequest request) {

        Integer userId=GetUser.getUserId(request);
        if(userId==null){
            return ResponseUtil.unlogin();
        }
        Order order=new Order();
        order.setId(id);
        order.setStatusCode(6);
        Object object=orderService.alterOrderStatus(order,userId);
        return object;
    }


    /**
     * 支付注定的订单
     * @param id    要支付的订单的id
     * @return      支付结果
     */
    @PostMapping("/orders/{id}/payment")
    public Object createPayment(@PathVariable Integer id){

        Object object=orderService.createPayment(id);
        return object;
    }

    /**
     *订单退款
     * @param request   网络请求
     * @param id        要退货的订单的id
     * @return  Order   订单操作结果
     */
    @PostMapping("/orders/{id}/refund")
    public Object refundOrder(@RequestBody OrderItem orderItem, @PathVariable Integer id, HttpServletRequest request) {
        orderItem.setId(id);
        orderItem.setStatusCode(5);
        OrderItem newOrderItem=orderService.alterOrderItemStatus(orderItem);
        if(newOrderItem!=null){
            Object object=ResponseUtil.ok(newOrderItem);
            return object;
        }else{
            return ResponseUtil.updatedDataFailed();
        }
    }


    /**
     * 管理员查看订单
     * @param userId                指定的用户
     * @param page                  页数
     * @param limit                 每页显示的数量
     * @param orderSn               订单号
     * @param orderStatusArray      订单类型
     * @return
     */
    @GetMapping("/admin/orders")
    public Object adminGetOrders(@RequestParam(value = "userId",defaultValue = "0") Integer userId, @RequestParam(value = "page",defaultValue = "1") Integer page,
                                 @RequestParam(value = "limit",defaultValue = "10") Integer limit, @RequestParam(value = "OrderSn",defaultValue = "") String orderSn,
                                 @RequestParam List<Short> orderStatusArray) {
        ArrayList<Order> result=new ArrayList<>();
        for(Short type:orderStatusArray){
            List<Order> temp=orderService.findOrdersByUserId(userId,page,limit,Integer.valueOf(type));
            for(int i=0;i<temp.size();i++){
                if("".equals(orderSn)){
                    break;
                }
                if(!temp.get(i).getOrderSn().equals(orderSn)){
                    temp.remove(i);
                }
            }
            result.addAll(temp);
        }
        Object object=ResponseUtil.ok(result);
        return object;
    }


    /**
     * 管理员根据订单号查询订单
     * @param id    订单id
     * @return  order
     */
    @GetMapping("/admin/orders/{id}")
    public Object adminGetOrder(@PathVariable Integer id){
        Order order=orderService.findOrderDetail(id);
        Object object=ResponseUtil.ok(order);
        return object;
    }


    /**
     * 管理员根据订单号处理退款
     *管理员处理退款
     * @param id        订单的id
     * @return  Order       操作结果
     */
    @PostMapping("/admin/orders/{id}/refund")
    public Object adminHandleRefund(@RequestBody OrderItem orderItem, @PathVariable Integer id) {
        orderItem.setId(id);
        orderItem.setStatusCode(6);
        OrderItem result=orderService.adminAlterOrderItemStatus(orderItem);
        if(result!=null){
            return ResponseUtil.ok(result);
        }else {
            return ResponseUtil.updatedDataFailed();
        }
    }

    /**
     *订单发货
     * @param id        要发货的订单
     * @return  Order
     */
    @PutMapping("/orders/{id}/ship")
    public Object orderShip(@PathVariable Integer id,HttpServletRequest request) {

        Integer userId=GetUser.getUserId(request);
        if(userId==null){
            return ResponseUtil.unlogin();
        }
        Order order=new Order();
        order.setId(id);
        order.setStatusCode(5);
        order.setShipTime(LocalDateTime.now());
        Object object=orderService.alterOrderStatus(order,userId);
        return object;
    }


    /**
     * ---------内部接口 ---------------
     */

    /**
     * 要退货的订单
     * @param orderItemId   orderItem的id
     * @return              orderItem
     */
    @PostMapping("/orderItem/{id}/exchange")
    public Object exchange(@PathVariable Integer orderItemId){
        Object object=orderService.orderItemexchange(orderItemId);
        return object;
    }

    /**
     * 根据orderItem的id来查询这是什么类型的订单
     * @param id        orderItem的id
     * @return          订单的类型
     */
    @GetMapping("/orderItem/goodsType")
    public Object findGoodsType(@RequestParam Integer id){
        Integer goodsType=orderService.findOrderItemById(id);
        return ResponseUtil.ok(goodsType);
    }

    /**
     *订单进行支付
     * @param id    订单的id
     * @return      支付状态
     */
    @PutMapping("/order/{id}")
    public boolean payFinish(@PathVariable Integer id){
        boolean state=orderService.finishPay();
        return state;
    }

    /**
     * 查询某团购规则完成了多少订单
     * @param grouponRulePo 团购规则
     * @return  orders的数量
     */
    @GetMapping("/orders/grouponOrders")
    public Object findGroupOnOrders(@RequestBody GrouponRulePo grouponRulePo){
        ArrayList<Order> orders=orderService.findOrdersByGroupRule(grouponRulePo);
        Object object=ResponseUtil.ok(orders.size());
        return object;
    }

    /**
     * 根据团购规则退款
     * @param grouponRulePo 团购规则
     * @param price         退款的比率
     * @return
     */
    @PostMapping("/order/grouponOrders/refund")
    public Object grouponRefund(@RequestBody GrouponRulePo grouponRulePo,@RequestParam BigDecimal price){
        orderService.grouponRefund(grouponRulePo,price);
        return ResponseUtil.ok();
    }

    /**
     * 预售被取消后的退款
     * @param presaleRule   预售规则
     * @return              退款情况
     */
    @GetMapping("/orders/presaleRulePo")
    public Object preSaleRefund(@RequestBody PresaleRule presaleRule){
        orderService.setPreSaleFund(presaleRule);
        return ResponseUtil.ok();
    }

}
