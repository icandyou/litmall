package litmall.shareservice.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import litmall.shareservice.domain.Log;

/**
 * @Author lsz
 * @create 2019/12/16 22:36
 */

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
