package litmall.orderservice.controller.otherInterface;

import domain.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author lsz
 * @create 2019/12/13 9:33
 */
//@FeignClient(value = "userInfoService",url = "forward:/")
public interface UserController {

    /**
     * 通过userId查询user
     * @param id    用户的id
     * @return  查询到的用户
     */
    @GetMapping("/user/id")
    public User findUserById(Integer id);

    /**
     * 设置返点
     * @param userId
     * @param rebate
     * @return
     */
    @PutMapping("/user/rebate")
    public boolean setRebate(@RequestParam Integer userId, @RequestParam Integer rebate);

}
