package litmall.shareservice.controller.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import litmall.shareservice.controller.LogController;
import litmall.shareservice.controller.ShareController;
import litmall.shareservice.domain.*;
import litmall.shareservice.service.ShareService;
import litmall.shareservice.util.GetUser;
import litmall.shareservice.util.ResponseUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;


/**
 * @author 3139华少楠
 *
 */
@RestController
public class ShareControllerImpl implements ShareController {
    @Resource
    private ShareService shareService;
    @Resource
    private LogController logController;
    private static final Logger logger = LoggerFactory.getLogger(ShareControllerImpl.class);

    @Override
    public Object calculateRebate(@RequestBody Order order, @RequestParam String createTime)
    {

        DateTimeFormatter df=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime timeC=LocalDateTime.parse(createTime);

        order.setGmtCreate(timeC);
        Map<Integer,Integer> rebateMap;

        rebateMap=shareService.calculateRebate(order);
        if(rebateMap!=null){
            Object rebateObj = ResponseUtil.ok(rebateMap);
            logger.debug("calculateRebate：" + rebateObj);
            return  rebateObj;
        }
        else{
            Object failObj = ResponseUtil.fail();
            logger.debug("计算返点失败"+failObj);
            return failObj;
        }

    }

    @Override
    public Object list(HttpServletRequest request, Integer id)throws UnknownHostException {

        Log log=new Log();
        InetAddress address=Inet4Address.getLocalHost();

        Integer userId = GetUser.getUserId(request);
        Integer roleId = GetUser.getRoleId(request);

        boolean isWrite=false;
        if(roleId!=null&& roleId!=0 && roleId!=4){
            isWrite=true;
        }
        ShareRule shareRule=shareService.getShareRule(id);

        log.setAdminId(userId);
        log.setIp(address.getHostAddress());
        log.setType(0);
        log.setActions("获取分享规则");

        if(shareRule.getId()>0) {
            Object shareObj = ResponseUtil.ok(shareRule);
            logger.debug("getShareRule的返回值：" + shareObj);
            log.setActionId(shareRule.getId());
            log.setStatusCode(1);
            if(isWrite){
            logController.writeLog(log);}
            return shareObj;
        }
        else if(shareRule.getId()==-1) {
            Object shareObj = ResponseUtil.fail(612,"商品已下架");
            logger.debug("getShareRule的返回值：" + shareObj);
            log.setActionId(null);
            log.setStatusCode(0);
            if(isWrite){
            logController.writeLog(log);}
            return shareObj;
        }
        else
        {
            Object failObj = ResponseUtil.fail(612,"系统内部错误");
            logger.debug("获取分享规则失败"+failObj);
            log.setActionId(null);
            log.setStatusCode(0);
            if(isWrite){
            logController.writeLog(log);}
            return failObj;
        }

    }


    @Override
    public Object create(HttpServletRequest request, ShareRulePo shareRulePo) throws UnknownHostException {

        Log log=new Log();
        InetAddress address = Inet4Address.getLocalHost();

        Integer userId=GetUser.getUserId(request);
        if(userId==null){
            Object failObj=ResponseUtil.unlogin();
            logger.debug("新建分享规则失败"+failObj);
            return failObj;
        }
        if(shareRulePo.getGoodsId()==null){
            Object failObj=ResponseUtil.fail(610,"无商品");
            logger.debug("新建分享规则失败"+failObj);
            return failObj;
        }
        if(shareRulePo.getShareLevelStrategy()==null){
            Object failObj=ResponseUtil.fail(610,"分享级别策略为空");
            logger.debug("新建分享规则失败"+failObj);
            return failObj;
        }


        log.setAdminId(userId);
        log.setIp(address.getHostAddress());
        log.setType(1);
        log.setActions("新建分享规则");

        ShareRulePo newShareRulePo=shareService.createShareRule(shareRulePo);
        if(newShareRulePo.getId()>0) {
            Object newShareObj = ResponseUtil.ok(newShareRulePo);
            logger.debug("createShareRule的返回值:" + newShareObj);
            log.setActionId(shareRulePo.getId());
            log.setStatusCode(1);
            logController.writeLog(log);
            return newShareObj;
            }
        else if(newShareRulePo.getId()==-1){
            Object failObj=ResponseUtil.fail(610,"该商品已下架");
            logger.debug("新建分享规则失败"+failObj);
            log.setActionId(shareRulePo.getId());
            log.setStatusCode(0);
            logController.writeLog(log);
            return failObj;
            }
        else if(newShareRulePo.getId()==-2){
            Object failObj=ResponseUtil.fail(610,"一个商品只能有一个分享规则");
            logger.debug("新建分享规则失败"+failObj);
            log.setActionId(shareRulePo.getId());
            log.setStatusCode(0);
            logController.writeLog(log);
            return failObj;
        }
        else if(newShareRulePo.getId()==-3){
            Object failObj=ResponseUtil.fail(610,"操作数据库失败");
            logger.debug("新建分享规则失败"+failObj);
            log.setActionId(shareRulePo.getId());
            log.setStatusCode(0);
            logController.writeLog(log);
            return failObj;
        }
        else {
            Object failObj=ResponseUtil.fail(610,"系统内部错误");
            logger.debug("新建分享规则失败"+failObj);
            log.setActionId(shareRulePo.getId());
            log.setStatusCode(0);
            logController.writeLog(log);
            return failObj;
        }
    }

    @Override
    public Object delete(HttpServletRequest request,Integer id) throws UnknownHostException {

        Log log=new Log();
        InetAddress address = Inet4Address.getLocalHost();

        Integer userId = GetUser.getUserId(request);
        //userId=1;
        if(userId==null)
        {
            Object failObj = ResponseUtil.unlogin();
            logger.debug("删除分享规则失败"+failObj);
            return failObj;
        }
        log.setAdminId(userId);
        log.setIp(address.getHostAddress());
        log.setType(3);
        log.setActions("删除分享规则");

        Integer deShare = shareService.deShareRule(id);
        Object deShareObj;
        if(deShare==1) {
            deShareObj=ResponseUtil.ok();
            logger.debug("删除分享规则成功");
            log.setActionId(id);
            log.setStatusCode(1);
            logController.writeLog(log);
        }
        else if(deShare==-1){
            deShareObj=ResponseUtil.fail(611,"该分享规则不存在");
            logger.debug("删除分享规则失败,该分享规则不存在");
            log.setActionId(id);
            log.setStatusCode(0);
            logController.writeLog(log);
        }
        else {
            deShareObj=ResponseUtil.fail(611,"系统内部错误");
            logger.debug("删除分享规则失败");
            log.setActionId(id);
            log.setStatusCode(0);
            logController.writeLog(log);
        }
        return deShareObj;
    }

    @Override
    public Object update(HttpServletRequest request,ShareRulePo shareRulePo, Integer id) throws UnknownHostException {

        Log log=new Log();
        InetAddress address = Inet4Address.getLocalHost();

        Integer userId = GetUser.getUserId(request);
        //userId=1;
        if(userId==null)
        {
            Object failObj = ResponseUtil.unlogin();
            logger.debug("更新分享规则失败"+failObj);
            return failObj;
        }
        if(shareRulePo.getGoodsId()==null && shareRulePo.getShareLevelStrategy()==null){
            Object failObj=ResponseUtil.fail(613,"修改内容为空");
            logger.debug("更新分享规则失败"+failObj);
            return failObj;
        }

        log.setAdminId(userId);
        log.setIp(address.getHostAddress());
        log.setType(2);
        log.setActions("更新分享规则");

        ShareRulePo newShareRulePo=shareService.alterShareRule(shareRulePo,id);
        if (newShareRulePo.getId()>0) {
            Object newShareObj = ResponseUtil.ok(newShareRulePo);
            logger.debug("修改分享规则的返回值" + newShareObj);
            log.setActionId(id);
            log.setStatusCode(1);
            logController.writeLog(log);
            return newShareObj;
        }
        else if(newShareRulePo.getId()==-1){
            Object failObj = ResponseUtil.fail(613,"该分享规则不存在");
            logger.debug("修改分享规则失败"+failObj);
            log.setActionId(id);
            log.setStatusCode(0);
            logController.writeLog(log);
            return  failObj;
        }
        else if(newShareRulePo.getId()==-2){
            Object failObj = ResponseUtil.fail(613,"该商品已下架");
            logger.debug("修改分享规则失败"+failObj);
            log.setActionId(id);
            log.setStatusCode(0);
            logController.writeLog(log);
            return  failObj;
        }
        else if(newShareRulePo.getId()==-3){
            Object failObj = ResponseUtil.fail(613,"操作数据库失败");
            logger.debug("修改分享规则失败"+failObj);
            log.setActionId(id);
            log.setStatusCode(0);
            logController.writeLog(log);
            return  failObj;
        }
        else if(newShareRulePo.getId()==-4){
            Object failObj = ResponseUtil.fail(613,"该商品已有分享规则");
            logger.debug("修改分享规则失败"+failObj);
            log.setActionId(id);
            log.setStatusCode(0);
            logController.writeLog(log);
            return  failObj;
        }
        else
        {
            Object failObj = ResponseUtil.fail(613,"系统内部错误");
            logger.debug("修改分享规则失败"+failObj);
            log.setActionId(id);
            log.setStatusCode(0);
            logController.writeLog(log);
            return  failObj;
        }
    }

    @Override
    public Object createSharedItems(HttpServletRequest request, BeSharedItem beSharedItem) {

        Integer userId = GetUser.getUserId(request);
        //userId=1;
        if(userId==null)
        {
            Object failObj = ResponseUtil.unlogin();
            logger.debug("新建用户的被分享条目失败"+failObj);
            return failObj;
        }
        if(beSharedItem.getGoodsId()==null){
            Object failObj=ResponseUtil.fail(614,"无商品");
            logger.debug("新建用户的被分享条目失败"+failObj);
            return failObj;
        }
        if(beSharedItem.getSharerId()==null){
            Object failObj=ResponseUtil.fail(614,"无分享人");
            logger.debug("新建用户的被分享条目失败"+failObj);
            return failObj;
        }
        if(beSharedItem.getBeSharedUserId()==null){
            Object failObj=ResponseUtil.fail(614,"无被分享人");
            logger.debug("新建用户的被分享条目失败"+failObj);
            return failObj;
        }
        logger.debug("controller");
        BeSharedItem newBeSharedItem = shareService.createBeShareItem(beSharedItem);
        if (newBeSharedItem .getId()>0) {
            Object newBeShareObj = ResponseUtil.ok(newBeSharedItem);
            logger.debug("createSharedItems的返回值：" + newBeShareObj);
            return newBeShareObj;
        }
        else if(newBeSharedItem .getId()==-1){
            Object failObj = ResponseUtil.fail(614,"该商品已下架");
            logger.debug("新建用户的被分享条目失败：" + failObj);
            return failObj;
        }
        else if(newBeSharedItem .getId()==-2){
            Object failObj = ResponseUtil.fail(614,"分享者不存在");
            logger.debug("新建用户的被分享条目失败：" + failObj);
            return failObj;
        }
        else if(newBeSharedItem .getId()==-3){
            Object failObj = ResponseUtil.fail(614,"操作数据库失败");
            logger.debug("新建用户的被分享条目失败：" + failObj);
            return failObj;
        }
        else if(newBeSharedItem .getId()==-4){
            Object failObj = ResponseUtil.fail(614,"相同的被分享条目只能创建一次");
            logger.debug("新建用户的被分享条目失败：" + failObj);
            return failObj;
        }
        else {
            Object failObj = ResponseUtil.fail(614,"系统内部错误");
            logger.debug("新建用户的被分享条目失败：" + failObj);
            return failObj;
        }

    }

}
