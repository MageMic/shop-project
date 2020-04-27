package com.zju.wsh.api;

import com.zju.wsh.entity.Result;
import com.zju.wsh.pojo.TradeGoods;
import com.zju.wsh.pojo.TradeUser;
import com.zju.wsh.pojo.TradeUserMoneyLog;

/**
 * @Author: zjumic
 * @Date: created at 21:24 2020/4/25
 */
public interface IUserService {

    TradeUser findOne(Long userId);

    Result updateMoneyPaid(TradeUserMoneyLog userMoneyLog);
}
