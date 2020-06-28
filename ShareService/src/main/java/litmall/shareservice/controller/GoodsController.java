package litmall.shareservice.controller;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author 模块标准组
 * @Description:商品模块外部及内部api
 * @create 2019/12/3 18:30
 */
@FeignClient(value = "goodsInfoService",url = "rl: http://47.100.91.153:3090")
public interface GoodsController {

    /**
     * 判断商品是否在售 - 内部
     * @param id
     * @return
     */
    @GetMapping("/goods/{id}/isOnSale")
    public boolean isGoodsOnSale(@PathVariable Integer id);

}
