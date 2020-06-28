package litmall.shareservice.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import litmall.shareservice.domain.BeSharedItem;
import litmall.shareservice.domain.ShareItem;
import litmall.shareservice.domain.ShareRulePo;

import java.util.ArrayList;

/**
 * @author 3139华少楠
 */
@Mapper
public interface ShareMapper {
    /**
     * 根据goodsId返回分享规则
     * @param id goodsId
     * @return 分享规则
     */
    ShareRulePo getShareRuleByGoods(Integer id);

    /**
     * 根据id返回分享规则
     * @param id shareRuleId
     * @return ShareRulePo
     */
    ShareRulePo getShareRuleById(Integer id);
    /**
     * create ShareRule
     * 新建分享规则
     * @param shareRulePo 分享规则
     * @return Integer
     */
    Integer createShareRule(ShareRulePo shareRulePo);

    /**
     * delete shareRule by id
     * 对shareRule进行逻辑删除，将is_deleted置1
     * @param id 分享规则id
     * @return shareRule
     */
    Integer deShareRule(Integer id);

    /**
     * 根据ID修改ShareRule
     * @param id 分享规则id
     * @param shareRulePo 分享规则
     * @return Integer
     */
    Integer alterShareRule(ShareRulePo shareRulePo, Integer id);

    /**
     * 新建用户的被分享表
     * @param beSharedItem 被分享条目
     * @return Integer
     */
    Integer createBeShareItem(BeSharedItem beSharedItem);

    /**
     * 查看订单中的商品是否属于被分享的商品
     * 且是否在有效时间内
     * 返回BeSharedItem
     * @param userId 被分享人id
     * @param goodsId 被分享商品id
     * @return ArrayList
     */
    ArrayList<BeSharedItem> checkBeSharedItem(@Param("beSharedId") Integer userId,
                                              @Param("goodsId") Integer goodsId);

    /**
     * 改变BeSharedItem的状态，从已分享0改为已返点1
     *
     * @param id 被分享条目的ID
     * @return Integer
     */
    Integer alterStatues(Integer id);

    /**
     * 根据分享者id和商品id是否有相应的shareItem
     *
     * @param sharerId 分享人id
     * @param goodsId 商品id
     * @return ShareItem
     */
    ShareItem checkSharedItem(@Param("sharerId") Integer sharerId, @Param("goodsId") Integer goodsId);

    /**
     * 增加shareItem
     * @param shareItem 分享条目
     * @return Integer
     */
    Integer createShareItem(ShareItem shareItem);
    /**
     * 根据分享者id和商品id增加shareItem的successNum
     *
     * @param shareItem 分享条目
     * @return Integer
     */
    Integer addShareSuccess(ShareItem shareItem);

    /**
     * 获得一个OrderItem所有的BeSharedItem
     * 按创建时间birthTime排序
     * @param goodsId 商品id
     * @param beSharedUserId 被分享人id
     * @return ArrayList
     */
    ArrayList<BeSharedItem> getSameBeShareItem(@Param("goodsId") Integer goodsId, @Param("beSharedId") Integer beSharedUserId);


    /**
     * 检查是否有
     * @param beSharedItem beSharedItem
     * @return BeSharedItem
     */
    BeSharedItem isExistSameBeSharedId(BeSharedItem beSharedItem);
}
