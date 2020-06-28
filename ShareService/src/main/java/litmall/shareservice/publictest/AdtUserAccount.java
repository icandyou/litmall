package litmall.shareservice.publictest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 可以修改用户名和密码的用户
 * @auther mingqiu
 * @date 2019/12/19 下午5:53
 */
@Component
public class AdtUserAccount extends BaseAccount{
    @Autowired
    private RestTemplate restTemplate;

    @Value("http://${oomall.host}:${oomall.port}/userInfoService/admin/login")
    private String url;

    private String userName;
    private String password;

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

    public void setUserName(String userName) {

        this.userName = userName;
        this.token = "";
    }

    public void setPassword(String password) {
        this.password = password;
        this.token = "";
    }
}
