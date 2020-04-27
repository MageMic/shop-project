package com.zju.wsh.goods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zju.wsh.api.IGoodsService;
import com.zju.wsh.constant.ShopCode;
import com.zju.wsh.entity.Result;
import com.zju.wsh.exception.CastException;
import com.zju.wsh.goods.mapper.TradeGoodsMapper;
import com.zju.wsh.goods.mapper.TradeGoodsNumberLogMapper;
import com.zju.wsh.pojo.TradeGoods;
import com.zju.wsh.pojo.TradeGoodsNumberLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

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
    @Autowired
    private TradeGoodsNumberLogMapper goodsNumberLogMapper;

    @Override
    public Result reduceGoodsNum(TradeGoodsNumberLog goodsNumberLog) {
        if (goodsNumberLog == null ||
                goodsNumberLog.getOrderId() == null ||
                goodsNumberLog.getGoodsId() == null ||
                goodsNumberLog.getGoodsNumber() == null ||
                goodsNumberLog.getGoodsNumber() <= 0) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        TradeGoods goods = tradeGoodsMapper.selectByPrimaryKey(goodsNumberLog.getGoodsId());
        if (goods.getGoodsNumber() < goodsNumberLog.getGoodsNumber()) {
            // 库存不足
            CastException.cast(ShopCode.SHOP_GOODS_NUM_NOT_ENOUGH);
        }
        // 减库存
        goods.setGoodsNumber(goods.getGoodsNumber() - goodsNumberLog.getGoodsNumber());
        tradeGoodsMapper.updateByPrimaryKey(goods);

        // 记录库存操作记录
        // 减库存，记录负数
        goodsNumberLog.setGoodsNumber(-(goodsNumberLog.getGoodsNumber()));
        goodsNumberLog.setLogTime(new Date());

        goodsNumberLogMapper.insert(goodsNumberLog);
        return new Result(ShopCode.SHOP_SUCCESS.getSuccess(), ShopCode.SHOP_SUCCESS.getMessage());
    }

    @Override
    public TradeGoods findOne(Long goodsId) {
        if (goodsId == null) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        return tradeGoodsMapper.selectByPrimaryKey(goodsId);
    }
}
