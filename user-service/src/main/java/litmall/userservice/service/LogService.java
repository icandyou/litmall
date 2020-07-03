package litmall.userservice.service;


import domain.Log;
import litmall.userservice.service.impl.LogServiceFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.feign.FeignClientsConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * @author liznsalt
 */
@Component
@FeignClient(
        name = "logService",
        url = "http://101.132.152.28:3410",
        decode404 = true,
        fallbackFactory = LogServiceFactory.class,
        configuration = FeignClientsConfiguration.class
)
public interface LogService {
    /**
     * 添加日志
     * @param log 日志
     * @return 结果
     */
    @RequestMapping(method = RequestMethod.POST, value = "/log")
    Object addLog(@RequestBody Log log);
}
