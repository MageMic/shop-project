package com.zju.wsh.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zju.wsh.api.IGoodsService;
import com.zju.wsh.constant.ShopCode;
import com.zju.wsh.exception.CastException;
import com.zju.wsh.mapper.TradeGoodsMapper;
import com.zju.wsh.pojo.TradeGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: zjumic
 * @Date: created at 21:19 2020/4/25
 */
// 定义为bean，然后将其定义为dubbo的服务，指定其服务接口
@Component
@Service(interfaceClass = IGoodsService.class)
public class GoodsServiceImpl implements IGoodsService {

    @Autowired
    private TradeGoodsMapper tradeGoodsMapper;

    @Override
    public TradeGoods findOne(Long goodsId) {
        if (goodsId == null) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        return tradeGoodsMapper.selectByPrimaryKey(goodsId);
    }
}
