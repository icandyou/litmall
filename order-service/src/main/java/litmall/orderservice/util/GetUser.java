package litmall.orderservice.util;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author lsz
 * @create 2019/12/15 19:37
 */
@Component
public class GetUser {

    public static   Integer getUserId(HttpServletRequest request){
        String userIdStr=request.getHeader("userId");
        if(userIdStr==null){
            return null;
        }
        return Integer.valueOf(userIdStr);
    }

}
