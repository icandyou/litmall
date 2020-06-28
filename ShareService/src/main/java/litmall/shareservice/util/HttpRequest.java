package litmall.shareservice.util;

import org.springframework.http.HttpHeaders;
import xmu.oomall.publictest.BaseAccount;

import java.net.URISyntaxException;

public class HttpRequest {
    /**
     * 生成包头
     * @param account 用户帐号
     * @return
     * @throws URISyntaxException
     */
    public static HttpHeaders getHttpHeaders(BaseAccount account) throws URISyntaxException {
        HttpHeaders headers = account.createHeaderWithToken();
        System.out.println("Generated Header = " + headers);
        if (headers == null) {
            //登录失败
            return null;
        }
        return headers;
    }
}
