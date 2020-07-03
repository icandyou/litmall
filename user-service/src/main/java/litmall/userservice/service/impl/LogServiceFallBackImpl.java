package litmall.userservice.service.impl;

import domain.Log;
import litmall.userservice.common.api.CommonResult;
import litmall.userservice.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * @author liznsalt
 */
@Component
@RequestMapping("/fallback/logService")
public class LogServiceFallBackImpl implements LogService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Object addLog(Log log) {
        logger.info("添加日志失败");
        return CommonResult.serious();
    }
}
