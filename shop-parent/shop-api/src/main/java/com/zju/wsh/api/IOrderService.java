package com.zju.wsh.api;

import com.zju.wsh.entity.Result;
import com.zju.wsh.pojo.TradeOrder;

/**
 * @Author: zjumic
 * @Date: created at 21:00 2020/4/25
 */
public interface IOrderService {

    /**
     * 下单接口
     * @param order
     * @return
     */
    Result confirmOrder(TradeOrder order);
}
