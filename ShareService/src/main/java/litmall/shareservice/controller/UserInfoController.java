package litmall.shareservice.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * AdminController
 *
 * @author YangHong
 * @date 2019-12-03
 */
@FeignClient(value = "userInfoService",url = "forward:/")
@RequestMapping("/myUser")
public interface UserInfoController {
 /**
  * 查看用户是否合法By userId
  * @param userId
  * @return shareRules
  */
 @GetMapping("/user/validate")
 public boolean isValid(@RequestParam Integer userId);
}
