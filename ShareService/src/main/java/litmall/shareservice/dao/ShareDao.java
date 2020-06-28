package litmall.shareservice.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import litmall.shareservice.domain.*;
import litmall.shareservice.domain.ShareRulePo;
import litmall.shareservice.mapper.ShareMapper;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author candy
 */

@Repository
public class ShareDao {
    private static final Logger logger = LoggerFactory.getLogger(ShareDao.class);

    @Resource
    private ShareMapper shareMapper;

    /**
     * 记录所有有效BeSharedItem
     */
    private ArrayList<BeSharedItem> validBeSharedItem = new ArrayList<>();
    /**
     * 记录该订单中所有有效的分享商品
     */
    private ArrayList<Integer> validSharedGoods = new ArrayList<>();
    /**
     * 记录所有增加了successNum的SharedItem
     */
    private ArrayList<ShareItem> shareItemArrayList = new ArrayList<>();

    /**
     * 计算用户应得的返点
     * @param order 订单
     * @return Map 每个分享者应该得到多少返点
     */
    public Map<Integer,Integer> calculateRebate(Order order){


        Integer rebate;

        List<OrderItem> orderItemList=order.getOrderItemList();

        HashMap<Integer,Integer> rebateMap=new HashMap<>(shareItemArrayList.size());
        HashMap<Integer, BigDecimal> goodPrice=new HashMap<>(validSharedGoods.size());

        //记录goodsID和price的对应关系
        for(OrderItem orderItem: orderItemList )
        {
            BigDecimal price=new BigDecimal(orderItem.getPrice().toString());
            goodPrice.put(orderItem.getGoodsId(),price);
            logger.debug("orderItem"+orderItem.getPrice().toString());
        }

        logger.debug(String.valueOf(shareItemArrayList.size()));
        //根据shareItem得到相应分享规则
        for(ShareItem shareItem:shareItemArrayList)
        {
            //得到shareRule
            ShareRule shareRule=new ShareRule(shareMapper.getShareRuleByGoods(shareItem.getGoodsId()));

            logger.debug("goodsPrice"+goodPrice.get(shareItem.getGoodsId()));
            //得到该条分享记录的商品价格
            BigDecimal price=new BigDecimal(goodPrice.get(shareItem.getGoodsId()).toString());

            logger.debug("price"+price.toString());
            //检查之前是否有该分享者的返点记录
            //检查rebateMap集合中有没有包含Key为userId的元素，如果有则返回true，否则返回false。
            boolean isExist=rebateMap.containsKey(shareItem.getUserId());
            logger.debug(String.valueOf(isExist));
            //如果之前没有记录过这个分享者
            if(!isExist) {
                rebate=shareRule.calculateRebate(shareItem,price);
                if(rebate==null){
                    continue;
                }
                rebateMap.put(shareItem.getUserId(), rebate);
            }
            //如果之前记录过，则用该条记录的返点加上之前的返点
            else {
                Integer curRebate=rebateMap.get(shareItem.getUserId());

                rebate=shareRule.calculateRebate(shareItem,price);
                if(rebate==null){
                    continue;
                }
                rebate+=curRebate;
                rebateMap.put(shareItem.getUserId(),rebate);
                }
            }

        return rebateMap;
    }

    /**
     * 查看订单中的OrderItem是否满足条件
     * 被分享者在有效时间内下单
     * 且下单后七天内未退货
     * 得到有效BeSharedItem
     * @param order 未退货订单
     */
    public void getValidBeSharedItem(Order order)
    {
        Integer userId = order.getUserId();
        List<OrderItem> orderItemList=order.getOrderItemList();
        for(OrderItem orderItem:orderItemList)
        {
            orderItem.setGmtCreate(order.getGmtCreate());
            //检查该订单中的商品是否属于该用户被分享的商品

             //记录所有BeSharedItem
            logger.debug(userId.toString());
            logger.debug(orderItem.getGoodsId().toString());
            ArrayList<BeSharedItem> beSharedItemList = shareMapper.checkBeSharedItem(userId, orderItem.getGoodsId());

            logger.debug("size"+beSharedItemList.size());
            if(beSharedItemList.size()>0) {
                for(BeSharedItem beSharedItem: beSharedItemList) {
                    //检查分享时间是否在下单时间之前
                    logger.debug("分享成功时间："+beSharedItem.getGmtCreate().toString());
                    logger.debug("下单时间："+beSharedItem.toString());

                    if (orderItem.getGmtCreate().isAfter(beSharedItem.getBirthTime())) {
                        logger.debug("after");
                        validBeSharedItem.add(beSharedItem);
                        //得到有效的被分享商品
                        if(!validSharedGoods.contains(beSharedItem.getGoodsId()))
                        {
                            validSharedGoods.add(beSharedItem.getGoodsId());
                        }
                    }
                }
            }
        }
        logger.debug("goods"+validSharedGoods.toString());
        logger.debug("shareItem"+validBeSharedItem.toString());
    }
    /**
     * 增加shareItem的successNum
     * 改变BeSharedItem的状态，从已分享0改为已返点1
     * @param beSharedUserId 被分享者id
     */
    public void addShareSuccess(Integer beSharedUserId) {
        //第一种情况，返给在有效时间内最早分享成功的用户
        //第二种情况，平分给所有有效时间内分享成功的用户
        Integer successNum=100;

        ArrayList<BeSharedItem> beSharedItems;
        ShareItem shareItem;

        for(Integer goodsId:validSharedGoods)
        {
            //得到有效分享商品的所有有效BeSharedItem
            beSharedItems=shareMapper.getSameBeShareItem(goodsId,beSharedUserId);
            //检查是否有效
            for(BeSharedItem beSharedItem:beSharedItems)
            {
                //如果有效BeSharedItem列表中不存在该对象，则删除
                if(!validBeSharedItem.contains(beSharedItem))
                {
                    beSharedItems.remove(beSharedItem);
                }
            }

            logger.debug(goodsId.toString());
            ShareRule shareRule=new ShareRule(shareMapper.getShareRuleByGoods(goodsId));

            if(shareRule.getShareType()==1)
            {//第一种情况，平分
                // 把successNum根据分享人数量平分
                int n=beSharedItems.size();
                if(n<=100) {successNum/=n;}
                else {
                    //若分享人数量大于100，则只取前100人
                    successNum/=100;
                    n=100;}
                logger.debug(successNum.toString());
                for(int i=0;i<n;i++)
                {
                    //改变beSharedItem的状态,从已分享0改为已返点1
                    if(shareMapper.alterStatues(beSharedItems.get(i).getId())==0){
                        logger.debug("改变beSharedItem的状态失败");
                    }
                    //为每个分享者增加successNum
                    shareItem=shareMapper.checkSharedItem(beSharedItems.get(i).getSharerId(), goodsId);
                    if (shareItem == null) {
                        if(shareMapper.createShareItem(beSharedItems.get(i).setShareItem(successNum))==0){
                            logger.debug("创建shareItem失败");
                        }
                        shareItem=beSharedItems.get(i).setShareItem(successNum);
                    }
                    else{
                        if(shareMapper.addShareSuccess(beSharedItems.get(i).setShareItem(successNum))==0){
                            logger.debug("更新shareItem失败");
                        }
                        shareItem=beSharedItems.get(i).setShareItem(successNum);
                    }
                    logger.debug("shareItem"+shareItem);
                    shareItemArrayList.add(shareItem);
                }
            }
            else
            {//第二种情况，只记第一个
                if(shareMapper.alterStatues(beSharedItems.get(0).getId())==0){
                    logger.debug("改变beSharedItem的状态失败");
                }
                shareItem=shareMapper.checkSharedItem(beSharedItems.get(0).getSharerId(), goodsId);
                if(shareItem != null) {
                    if(shareMapper.createShareItem(beSharedItems.get(0).setShareItem(successNum))==0){
                        logger.debug("创建shareItem失败");
                    }
                    shareItem=beSharedItems.get(0).setShareItem(successNum);
                }
                else{
                    if(shareMapper.addShareSuccess(beSharedItems.get(0).setShareItem(successNum))==0){
                        logger.debug("更新shareItem失败");
                    }
                    shareItem=beSharedItems.get(0).setShareItem(successNum);
                }
                logger.debug("shareItem"+shareItem);
                shareItemArrayList.add(shareItem);
            }
        }
    }



    /**
     * 通过goodsId查看分享规则
     * @param id 商品id
     * @return 返回shareRule
     */
    public ShareRule getShareRule(Integer id)
    {
        ShareRulePo shareRulePo=shareMapper.getShareRuleByGoods(id);
        return new ShareRule(shareRulePo);

    }

    /**
     * 新建分享规则
     * @param shareRulePo 分享规则
     * @return 分享规则
     */
    public ShareRulePo createShareRule(ShareRulePo shareRulePo)
    {
        //一个商品只能有一个分享规则
        if(shareMapper.getShareRuleByGoods(shareRulePo.getGoodsId())==null)
        {
            if(shareMapper.createShareRule(shareRulePo)!=0)
            {
                return shareRulePo;
            }
            shareRulePo.setId(-3);
            logger.debug("创建分享规则失败，操作数据库失败");
            return shareRulePo;
        }
        shareRulePo.setId(-2);
        logger.debug("一个商品只能有一个分享规则");
        return shareRulePo;
    }

    public Integer deShareRule(Integer id)
    {
        //查看shareRule是否存在
        if(shareMapper.getShareRuleById(id)!=null){
            return shareMapper.deShareRule(id);
        }
        logger.debug("该分享规则不存在");
        return -1;
    }

    public ShareRulePo alterShareRule(ShareRulePo shareRulePo, Integer id)
    {
        if(shareMapper.getShareRuleById(id)!=null)  {
            if(shareRulePo.getGoodsId()!=null){
                if(shareMapper.getShareRuleByGoods(shareRulePo.getGoodsId())!=null){
                    logger.debug("该商品已有分享规则");
                    shareRulePo.setId(-4);
                    return shareRulePo;
                    }
                if(shareMapper.alterShareRule(shareRulePo, id) != 0) {
                    return shareRulePo;
                    }
                else {
                    shareRulePo.setId(-3);
                    return shareRulePo;
                    }
                }
                if (shareMapper.alterShareRule(shareRulePo, id) != 0) {
                    return shareRulePo;
                }
                else {
                    shareRulePo.setId(-3);
                    return shareRulePo;
                }
            }

        else{
            logger.debug("该分享规则不存在");
            shareRulePo.setId(-1);
            return shareRulePo;
        }
    }

    public BeSharedItem createBeShareItem(BeSharedItem beSharedItem) {
        if (shareMapper.isExistSameBeSharedId(beSharedItem) == null) {
            if (shareMapper.createBeShareItem(beSharedItem) != null) {
                return beSharedItem;
            } else {
                beSharedItem.setId(-3);
                logger.debug("操作数据库失败");
                return beSharedItem;
            }
        } else {
                beSharedItem.setId(-4);
                logger.debug("相同的被分享条目只能创建一次");
                return beSharedItem;
        }
    }
}
