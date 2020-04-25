package com.zju.wsh.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zju.wsh.api.IUserService;
import com.zju.wsh.constant.ShopCode;
import com.zju.wsh.exception.CastException;
import com.zju.wsh.pojo.TradeUser;
import com.zju.wsh.user.mapper.TradeUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: zjumic
 * @Date: created at 21:27 2020/4/25
 */
@Component
@Service(interfaceClass = IUserService.class)
public class UserServiceImpl implements IUserService {
    @Autowired
    private TradeUserMapper tradeUserMapper;
    @Override
    public TradeUser findOne(Long userId) {
        if (userId == null) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        return tradeUserMapper.selectByPrimaryKey(userId);
    }
}
