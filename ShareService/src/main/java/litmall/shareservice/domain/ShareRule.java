package litmall.shareservice.domain;

import com.google.gson.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * @author candy
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class ShareRule extends ShareRulePo {
    @Getter
    @Setter
    public static class Strategy{
        private Integer lowerBound;
        private Integer upperBound;
        private BigDecimal discountRate;
    }
    private static class MyStrategy{
        private Integer lowerbound;
        private Integer upperbound;
        private BigDecimal rate;
    }
    private List<Strategy> strategyList;
    private Integer shareType;

    private static final Logger logger = LoggerFactory.getLogger(ShareRule.class);
    public ShareRule(){}
    public ShareRule(ShareRulePo shareRulePo)
    {

        this.setShareLevelStrategy(shareRulePo.getShareLevelStrategy());

        this.toStrategyList();

        this.setGoodsId(shareRulePo.getGoodsId());
        this.setGmtModified(shareRulePo.getGmtModified());
        this.setGmtCreate(shareRulePo.getGmtCreate());
        this.setBeDeleted(shareRulePo.getBeDeleted());
        this.setId(shareRulePo.getId());


    }
    public void toStrategyList(){

        List<Strategy>strategies=new ArrayList<>();
        String shareLevelStrategy= this.getShareLevelStrategy();

        JsonObject jObject = new JsonParser().parse(shareLevelStrategy).getAsJsonObject();
        try{
        JsonArray array = jObject.get("strategy").getAsJsonArray();

        for(JsonElement jsonElement :array){

            Strategy strategy= new Strategy();
            JsonObject jo = jsonElement.getAsJsonObject();
            strategy.lowerBound = jo.get("lowerbound").getAsInt();
            strategy.upperBound = jo.get("upperbound").getAsInt();
            strategy.discountRate = new BigDecimal(jo.get("rate").getAsString());

            strategies.add(strategy);
        }

        strategies.sort(Comparator.comparingInt(s -> s.lowerBound));

        this.setStrategyList(strategies);}
        catch (Exception e){
            e.printStackTrace();
            logger.debug("shareLevelStrategy数据输入异常");
        }
        this.setShareType(jObject.get("type").getAsInt());
    }
    public void toShareLevelStrategy() {

        List<MyStrategy> myStrategyList=new ArrayList<>();

        for(int i=0;i<this.getStrategyList().size();i++){
            MyStrategy myStrategy= new MyStrategy();
            myStrategy.lowerbound=this.getStrategyList().get(i).lowerBound;
            myStrategy.upperbound=this.getStrategyList().get(i).upperBound;
            myStrategy.rate=this.getStrategyList().get(i).discountRate;
            myStrategyList.add(myStrategy);
            logger.debug("myStrategy:"+myStrategy.lowerbound);
            logger.debug("myStrategy:"+myStrategy.upperbound);
            logger.debug("myStrategy:"+myStrategy.rate);
        }
        Gson gson=new Gson();

        String s=gson.toJson(myStrategyList);
        JsonObject object=new JsonObject();
        JsonArray jsonArray = new JsonParser().parse(s).getAsJsonArray();
        object.add("strategy",jsonArray);
        object.add("type",gson.toJsonTree(gson.toJson(0)));

        logger.debug(object.toString());
        this.setShareLevelStrategy(object.toString());
    }

    public Integer calculateRebate(ShareItem shareItem,BigDecimal price) {
        if(this.getStrategyList()==null){
            this.toStrategyList();
        }
        logger.debug(price.toString());

        //根据successNum所在区间得到返点比例
        Integer successNum=shareItem.getSuccessNum();
        Integer extend=100;
        logger.debug(this.getStrategyList().get(0).lowerBound.toString());
        //如果成功次数小于规则所规定策略的最低值，无返点
        if (successNum < this.getStrategyList().get(0).lowerBound * extend) {
            return null;
        }
        else {
            int n = this.getStrategyList().size();
            int level = 0;
            //找到分享记录所对应的区间
            for (int i = 0; i < n; i++) {
                if (successNum > this.getStrategyList().get(i).lowerBound * extend) {
                    level++;
                } else {
                    break;
                }
            }

            logger.debug("level"+level);
            if (level == n) {
                level = n - 1;
            }
            BigDecimal discountRate=this.getStrategyList().get(level).discountRate;
            logger.debug("discount:"+discountRate.toString());
            BigDecimal xRebate=new BigDecimal("100");
            Integer rebate;
            discountRate=discountRate.multiply(xRebate);
            xRebate=price.multiply(discountRate);
            // 向下取整
            rebate=xRebate.setScale(0, BigDecimal.ROUND_DOWN ).intValue();
            rebate *=(successNum-this.getStrategyList().get(level).lowerBound);
            return rebate;
        }
    }


}
