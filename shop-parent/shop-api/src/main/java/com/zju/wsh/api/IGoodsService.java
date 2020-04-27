package com.zju.wsh.api;

import com.zju.wsh.entity.Result;
import com.zju.wsh.pojo.TradeGoods;
import com.zju.wsh.pojo.TradeGoodsNumberLog;

/**
 * @Author: zjumic
 * @Date: created at 21:17 2020/4/25
 */
public interface IGoodsService {

    TradeGoods findOne(Long goodsId);

    /**
     * 扣减库存
     * @param goodsNumberLog
     * @return
     */
    Result reduceGoodsNum(TradeGoodsNumberLog goodsNumberLog);
}
