package com.example.demo.controller;



import com.example.demo.VO.OrderPaymentVo;
import com.example.demo.domain.OrderPo;
import com.example.demo.domain.Payment;
import com.example.demo.service.PaymentService;

import com.example.demo.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chei1
 */
@RestController("")
public class PaymentController {
    @Autowired
    PaymentService paymentService;


    @RequestMapping(value = "/payment", method = RequestMethod.POST)
    public Object addPayment(OrderPo orderPo){

        OrderPo retBody=paymentService.addPayment(orderPo);
        Object retObj = ResponseUtil.ok(retBody);
        System.out.println("submit的返回值："+retObj);
        return retBody;
    }

    @RequestMapping(value = "/payments",method =RequestMethod.PUT)
    public String putPayment(String paySn){
        String retBody=paymentService.putPayment(paySn);
        Object retObj = ResponseUtil.ok(retBody);
        System.out.println("submit的返回值："+retObj);
        return retObj.toString();
    }


}

//    @PostMapping("payments")
//    @ApiOperation("下单，获取prepay_id等五个参数，进而鉴权调起支付")
//    public Object addPayment(@ApiParam(name="orderId",value="商户订单号",required=true) String orderId){
//        Map<String, Object> addPaymentRet = new HashMap<String, Object>();
//        return ResponseUtil.ok(addPaymentRet);
//    }
//
//    @PutMapping("payments/{id}")
//    @ApiOperation("微信后台向商户系统推送支付结果，商户系统修改订单状态")
//    public boolean updatePayment(@ApiParam(name="return_code",value="SUCCESS/FAIL",required=true) String return_code,
//                                @ApiParam(name="return_msg",value="返回信息，如非空，为错误原因",required=false) String return_msg,
//                                @ApiParam(name="orderId",value="商户订单号",required=true) @PathVariable("id") String orderId){
//        return true;
//    }
//@PostMapping("payment")
//public Object addPayment(@Requestbody OrderPaymentVo orderPaymentVo);
//
//
//
//    /**
//     * 管理员删除支付（好像没什么用？）
//     *
//     * @param paymentId
//     * @return Payment
//     */
//    @DeleteMapping("payment/{id}")
//    public Object deletePayment(@PathVariable("id") Integer paymentId);
//
//
//
//    /**
//     * （模拟的）微信后台调用此方法修改订单状态
//     * 此方法还会调用order模块的updateOrder方法，修改订单状态
//     *
//     * @param prepay_id：预支付订单号
//     * @return Payment
//     */
//    @PutMapping("payment/{id}")
//    public Object updatePayment(@PathVariable("id") Integer prepay_id);
//
//
//    /**
//     * 管理员查看所有支付（用户好像不用看？）
//     *
//     * @param getPaymentVo
//     * @return List<GetPaymentVo>
//     */
//    @GetMapping("admin/payment")
//    public Object adminGetAllPayments(GetPaymentVo getPaymentVo, @RequestParam Integer userId);
//
//
//
//    /**
//     * 管理员查看某个支付（用户好像不用看？）
//     *
//     * @param paymentId
//     * @return GetPaymentVo
//     */
//    @GetMapping("admin/payment/{id}")
//    public Object adminGetAllPayments(@PathVariable("id") Integer paymentId, @RequestParam Integer userId);
//
