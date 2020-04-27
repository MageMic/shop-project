package com.zju.wsh.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zju.wsh.api.IUserService;
import com.zju.wsh.constant.ShopCode;
import com.zju.wsh.entity.Result;
import com.zju.wsh.exception.CastException;
import com.zju.wsh.pojo.TradeUser;
import com.zju.wsh.pojo.TradeUserMoneyLog;
import com.zju.wsh.pojo.TradeUserMoneyLogExample;
import com.zju.wsh.user.mapper.TradeUserMapper;
import com.zju.wsh.user.mapper.TradeUserMoneyLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: zjumic
 * @Date: created at 21:27 2020/4/25
 */
@Component
@Service(interfaceClass = IUserService.class)
public class UserServiceImpl implements IUserService {
    @Autowired
    private TradeUserMapper tradeUserMapper;
    @Autowired
    private TradeUserMoneyLogMapper userMoneyLogMapper;

    @Override
    public Result updateMoneyPaid(TradeUserMoneyLog userMoneyLog) {
        //1.校验参数是否合法
        if(userMoneyLog == null ||
                userMoneyLog.getUserId() == null ||
                userMoneyLog.getOrderId() == null ||
                userMoneyLog.getUseMoney() == null||
                userMoneyLog.getUseMoney().compareTo(BigDecimal.ZERO) <= 0){
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }

        //2.查询订单余额使用日志
        TradeUserMoneyLogExample userMoneyLogExample = new TradeUserMoneyLogExample();
        TradeUserMoneyLogExample.Criteria criteria = userMoneyLogExample.createCriteria();
        criteria.andOrderIdEqualTo(userMoneyLog.getOrderId());
        criteria.andUserIdEqualTo(userMoneyLog.getUserId());
        int r = userMoneyLogMapper.countByExample(userMoneyLogExample);

        TradeUser tradeUser = tradeUserMapper.selectByPrimaryKey(userMoneyLog.getUserId());

        //3.扣减余额...
        if(userMoneyLog.getMoneyLogType().intValue() == ShopCode.SHOP_USER_MONEY_PAID.getCode().intValue()){
            if(r > 0){
                //已经付款
                CastException.cast(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY);
            }
            //减余额
            tradeUser.setUserMoney(new BigDecimal(tradeUser.getUserMoney()).subtract(userMoneyLog.getUseMoney()).longValue());
            tradeUserMapper.updateByPrimaryKey(tradeUser);
        }
        //4.回退余额...
        if(userMoneyLog.getMoneyLogType().intValue() == ShopCode.SHOP_USER_MONEY_REFUND.getCode().intValue()){
            if(r < 0){
                //如果没有支付,则不能回退余额
                CastException.cast(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY);
            }
            //防止多次退款
            TradeUserMoneyLogExample userMoneyLogExample2 = new TradeUserMoneyLogExample();
            TradeUserMoneyLogExample.Criteria criteria1 = userMoneyLogExample2.createCriteria();
            criteria1.andOrderIdEqualTo(userMoneyLog.getOrderId());
            criteria1.andUserIdEqualTo(userMoneyLog.getUserId());
            criteria1.andMoneyLogTypeEqualTo(ShopCode.SHOP_USER_MONEY_REFUND.getCode());
            int r2 = userMoneyLogMapper.countByExample(userMoneyLogExample2);
            if(r2 > 0){
                CastException.cast(ShopCode.SHOP_USER_MONEY_REFUND_ALREADY);
            }
            //退款
            tradeUser.setUserMoney(new BigDecimal(tradeUser.getUserMoney()).add(userMoneyLog.getUseMoney()).longValue());
            tradeUserMapper.updateByPrimaryKey(tradeUser);
        }
        //5.记录订单余额使用日志
        userMoneyLog.setCreateTime(new Date());
        userMoneyLogMapper.insert(userMoneyLog);
        return new Result(ShopCode.SHOP_SUCCESS.getSuccess(),ShopCode.SHOP_SUCCESS.getMessage());
    }

    @Override
    public TradeUser findOne(Long userId) {
        if (userId == null) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        return tradeUserMapper.selectByPrimaryKey(userId);
    }
}
