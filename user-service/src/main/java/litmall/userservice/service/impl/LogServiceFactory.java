package litmall.userservice.service.impl;

import feign.hystrix.FallbackFactory;
import litmall.userservice.service.LogService;
import org.springframework.stereotype.Component;

/**
 * @author liznsalt
 */
@Component
public class LogServiceFactory implements FallbackFactory<LogService> {
    private final LogServiceFallBackImpl logServiceFallBack;

    public LogServiceFactory(LogServiceFallBackImpl logServiceFallBack) {
        this.logServiceFallBack = logServiceFallBack;
    }

    @Override
    public LogService create(Throwable throwable) {
        return logServiceFallBack;
    }
}
