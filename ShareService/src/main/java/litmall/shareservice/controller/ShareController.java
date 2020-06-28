package litmall.shareservice.controller;

import org.springframework.web.bind.annotation.*;
import litmall.shareservice.domain.BeSharedItem;
import litmall.shareservice.domain.Order;
import litmall.shareservice.domain.ShareRulePo;

import javax.servlet.http.HttpServletRequest;
import java.net.UnknownHostException;


/**
 * @author 3139华少楠
 */
@RestController
public interface ShareController {
    /**
     * 查看分享规则 by id（内外共用）
     *
     * @param id      商品id
     * @param request http请求
     * @return shareRules
     * @exception Exception
     */
    @GetMapping("/goods/{id}/shareRules")
    public Object list(HttpServletRequest request, @PathVariable Integer id) throws UnknownHostException;

    /**
     * 新建分享规则（外部）
     *
     * @param shareRulePo 可传入参数有shareLevelStrategy、goodsId
     * @param request     http请求
     * @return shareRules
     * @exception Exception
     */
    @PostMapping("/shareRules")
    public Object create(HttpServletRequest request, @RequestBody ShareRulePo shareRulePo) throws UnknownHostException;

    /**
     * 删除分享规则 by id（外部）
     *
     * @param id      分享规则id
     * @param request http请求
     * @return boolean
     * @exception Exception
     */
    @DeleteMapping("/shareRules/{id}")
    public Object delete(HttpServletRequest request, @PathVariable Integer id) throws UnknownHostException;

    /**
     * 修改分享规则 by id（外部）
     *
     * @param shareRulePo 可传入参数有shareLevelStrategy、goodsId
     * @param id          shareRule的id
     * @param request     http请求
     * @return shareRule
     * @exception Exception
     */
    @PutMapping("/shareRules/{id}")
    public Object update(HttpServletRequest request, @RequestBody ShareRulePo shareRulePo, @PathVariable Integer id) throws UnknownHostException ;

    /**
     * 增加某个用户的被分享表（外部）
     * 用户被分享时系统为其创建被分享表，用户id在状态里，分享者id在二维码里
     *
     * @param beSharedItem 可传入参数有sharerId、beSharedUserId、goodsId
     * @param request      http请求
     * @return beSharedItem
     */
    @PostMapping("/beSharedItems")
    public Object createSharedItems(HttpServletRequest request, @RequestBody BeSharedItem beSharedItem);

    /**
     * 计算返点（内部）
     * @param order 订单项
     * @param createTime 订单创建时间
     * @return  Integer
     */
    @PostMapping("/rebate")
    public Object calculateRebate(@RequestBody Order order, @RequestParam String createTime);
}
