package litmall.orderservice.controller.otherInterface;

import domain.Order;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author xyt
 * @create 2019/12/4 13:00
 */

//@FeignClient(value = "shareService",url = "http://106.15.249.35:3333")
public interface ShareController {

    /**
     *
     * @param
     * @return  Integer
     */
    @PostMapping("/rebate")
    public Object calculateRebate(@RequestBody Order order, @RequestParam String createTime);


}