package litmall.orderservice.controller.otherInterface;

import domain.Order;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author lsz
 * @create 2019/12/13 20:38
 */
//@FeignClient(value = "freightService", url = "http://60.205.212.233:3701")
public interface FrightController {


    /**
     * 根据order来计算运费
     * @param order
     * @return
     */
    @PostMapping("/freightPrice")
    public Object getFright(@RequestBody Order order);

}
