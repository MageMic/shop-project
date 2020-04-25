package com.zju.wsh.api;

import com.zju.wsh.pojo.TradeGoods;

/**
 * @Author: zjumic
 * @Date: created at 21:17 2020/4/25
 */
public interface IGoodsService {

    TradeGoods findOne(Long goodsId);
}
