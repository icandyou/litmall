package litmall.shareservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import litmall.shareservice.controller.GoodsController;
import litmall.shareservice.controller.UserInfoController;
import litmall.shareservice.dao.ShareDao;
import litmall.shareservice.domain.BeSharedItem;
import litmall.shareservice.domain.Order;
import litmall.shareservice.domain.ShareRule;
import litmall.shareservice.domain.ShareRulePo;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author 3139华少楠
 * @create 2019/12/12 22:05
 */
@Service
public class ShareService {


    @Resource
    ShareDao shareDao;
    @Resource
    UserInfoController userInfoController;
    @Resource
    GoodsController goodsController;


    private static final Logger logger = LoggerFactory.getLogger(ShareService.class);
    public Map<Integer,Integer> calculateRebate(Order order)
    {
        Integer beSharedId=order.getUserId();

        Map <Integer,Integer> rebateMap;
        //得到有效的BeSharedItem
        shareDao.getValidBeSharedItem(order);
        //根据返点方式增加shareItem的successNum
        shareDao.addShareSuccess(beSharedId);
        //计算每个分享者的返点
        rebateMap=shareDao.calculateRebate(order);
        //改变beShareItem的状态，从已分享0改为已返点1
        //shareDao.alterStatues();
        return rebateMap;

    }

    public BeSharedItem createBeShareItem(BeSharedItem beSharedItem)
    {
        //判断sharer是否合法
        logger.debug("service");
        logger.debug(beSharedItem.getSharerId().toString());
        if(!userInfoController.isValid(beSharedItem.getSharerId())){
            logger.debug("分享者不存在");
            beSharedItem.setId(-2);
            return beSharedItem;
        }
        //判断goods是否在售
        if(!goodsController.isGoodsOnSale(beSharedItem.getGoodsId())) {
            logger.debug("该商品已下架");
            beSharedItem.setId(-1);
            return beSharedItem;
        }

        try {
            return shareDao.createBeShareItem(beSharedItem);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    public ShareRule getShareRule(Integer id) {
        ShareRule shareRule;
        try {
            shareRule=shareDao.getShareRule(id);

            //检查goods是否在售
            if (goodsController.isGoodsOnSale(shareRule.getGoodsId())) {
            //if(true){
                return shareRule;
            }

            logger.debug("商品已下架");
            shareRule.setId(-1);
            return shareRule;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ShareRulePo createShareRule(ShareRulePo shareRulePo)
    {
        //检查goods是否在售
        if(goodsController.isGoodsOnSale(shareRulePo.getGoodsId()))
        {
            try {
                return shareDao.createShareRule(shareRulePo);
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        logger.debug("该商品已下架");
        shareRulePo.setId(-1);
        return shareRulePo;
    }

    public Integer deShareRule(Integer id)
    {
        return shareDao.deShareRule(id);
    }

    public ShareRulePo alterShareRule(ShareRulePo shareRulePo,Integer id)
    {
        //检查goods是否在售
        if(goodsController.isGoodsOnSale(shareRulePo.getGoodsId()))
        {
            try {
                return shareDao.alterShareRule(shareRulePo, id);
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        logger.debug("该商品已下架");

        shareRulePo.setId(-2);
        return shareRulePo;
    }

}
