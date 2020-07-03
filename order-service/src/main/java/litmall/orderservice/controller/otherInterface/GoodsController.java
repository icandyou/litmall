package litmall.orderservice.controller.otherInterface;


import domain.OrderItem;
import domain.Product;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author lsz
 * @create 2019/12/13 8:23
 */
//@FeignClient(value = "goodsInfoService",url = "http://47.100.91.153:3090")
public interface GoodsController {

    /**
     * 通过productId查询product
     * @param id
     * @return
     */
    @GetMapping("/user/product/{id}")
    public Product getProductById(@PathVariable Integer id);


    /**
     * 根据goodsId来获取goods对象
     * @param id
     * @return
     */
    @GetMapping("/goods/{id}")
    public Object getGoods(@PathVariable Integer id);

    /**
     * 根据orderItem说要操作哪个product，根据state来判断是增库存还是扣库存
     * @param state
     * @param orderItems
     * @return
     */
    @PostMapping("/products/decute")
    public Object updateDecute(@RequestParam("operation") boolean state, @RequestBody List<OrderItem> orderItems);

}
