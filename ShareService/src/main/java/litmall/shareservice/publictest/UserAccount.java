package litmall.shareservice.publictest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 普通用户帐号
 * @author  Ming Qiu
 * @date 2019/12/18
 */
@Component
public class UserAccount extends BaseAccount {

    @Value("http://${oomall.host}:${oomall.port}/userInfoService/login")
    private String url;

    @Value("${oomall.user}")
    private String userName;

    @Value("${oomall.password}")
    private String password;

    @Autowired
    private RestTemplate restTemplate;


    @Override
    protected String getUserName() {
        return this.userName;
    }

    @Override
    protected String getPassword() {
        return this.password;
    }

    @Override
    protected String getUrl() {
        return this.url;
    }

    @Override
    protected RestTemplate getRestTemplate() {
        return this.restTemplate;
    }
}
