package litmall.orderservice.controller.otherInterface;

import domain.Order;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author lsz
 * @create 2019/12/13 18:05
 */
//@FeignClient(value = "discountService",url = "http://60.205.212.233:3703")
public interface DiscountController {

    /**
     * 将order给discount模块让他判断和计算
     * @param order order参数
     * @return  修改过属性的order
     */
    @PostMapping("/discount/orders")
    public Object cabalDiscount(@RequestBody Order order);


}
