package litmall.orderservice.controller.otherInterface;

import domain.Log;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author lsz
 * @create 2019/12/16 22:36
 */
//@FeignClient("/logService")
public interface LogController {

    /**
     * 管理员写日志
     * @param log
     * @return
     */
    @PostMapping("/log")
    public Log writeLog(@RequestBody Log log);

}
