package litmall.shareservice.controller;

import domain.Log;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@FeignClient(value = "logsService",url = "url: http://101.132.152.28:3410")
public interface LogController {

    /**
     * 管理员写日志操作
     * @param log
     * @return
     */
    @PostMapping("/log")
    public Log writeLog(@RequestBody Log log);

}
