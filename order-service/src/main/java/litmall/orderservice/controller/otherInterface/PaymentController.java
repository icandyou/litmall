package litmall.orderservice.controller.otherInterface;

import domain.Payment;
import org.springframework.web.bind.annotation.*;

/**
 * @Author lsz
 * @create 2019/12/16 21:01
 */
//@FeignClient(value = "paymentService",url = "http://106.15.249.35:7777")
public interface PaymentController {

    /**
     * 生成payment
     * @param payment
     * @return
     */
    @PostMapping("/payment")
    public Object createPayment(@RequestBody Payment payment);

    /**
     *name
     * @param paySn
     * @return
     */
    @PutMapping("/payment/{id}")
    public Object payPayment(@PathVariable(name = "id") String paySn);

    /**
     * 根据orderId来获取相应的payment
     * @param id
     * @return
     */
    @GetMapping("/payment/{id}")
    public Object getPaymentByOrderId(@PathVariable("id") Integer id);


}
