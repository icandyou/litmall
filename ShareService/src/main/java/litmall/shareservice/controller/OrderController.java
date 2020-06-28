package litmall.shareservice.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * @author candy
 */
@FeignClient(value = "ordersService",url = "http://106.15.249.35:3302\n")

public interface OrderController {
    /**
     * 订单详情
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @return 订单详情
     */
    @GetMapping("/orders/{id}")
    public Object userDetail(Integer userId, Integer orderId);


}
