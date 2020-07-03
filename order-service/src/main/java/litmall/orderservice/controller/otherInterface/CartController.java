package litmall.orderservice.controller.otherInterface;/*
package 和 import 需要根据项目导入
 */

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户购物车服务
 * @author lsz
 */
//@FeignClient(value = "cartService",url = "http://47.98.145.177:3525")
public interface CartController {



    /**
     * 购物车商品货品数量
     * <p>
     * 如果用户没有登录，则返回空数据。
     *
     * @param userId 用户ID
     * @return 购物车商品货品数量
     */
    @GetMapping("/cartItem/{userId}")
    public Object userCart(@PathVariable Integer userId) ;

    /**
     * 根据购物车id查购物车
     * @param id
     * @return object
     */
    @GetMapping("/cartItems/{id}")
    public Object getCartById(@PathVariable Integer id);


    /**
     * 根据购物车id删除购物车
     * @param id
     * @return  object  删除成功情况
     */
    @DeleteMapping("/cartItems/{id}")
    public Object deleteCart(@PathVariable Integer id);

}