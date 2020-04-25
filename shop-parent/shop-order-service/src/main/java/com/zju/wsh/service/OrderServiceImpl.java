package com.zju.wsh.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.zju.wsh.api.IGoodsService;
import com.zju.wsh.api.IOrderService;
import com.zju.wsh.api.IUserService;
import com.zju.wsh.constant.ShopCode;
import com.zju.wsh.entity.Result;
import com.zju.wsh.exception.CastException;
import com.zju.wsh.pojo.TradeGoods;
import com.zju.wsh.pojo.TradeOrder;
import com.zju.wsh.pojo.TradeUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @Author: zjumic
 * @Date: created at 21:03 2020/4/25
 */
@Slf4j
@Component
@Service(interfaceClass = IOrderService.class)
public class OrderServiceImpl implements IOrderService {

    @Reference
    private IGoodsService goodsService;
    @Reference
    private IUserService userService;

    @Override
    public Result confirmOrder(TradeOrder order) {
        //1.校验订单
        checkOrder(order);
        //2.生成预订单

        try {
            //3.扣减库存

            //4.扣减优惠券

            //5.使用余额

            //6.确认订单

            //7.返回成功状态

        } catch (Exception e) {
            //1.确认订单失败,发送消息

            //2.返回失败状态
        }
        return null;
    }

    /**
     * 校验订单
      * @param order
     */
    private void checkOrder(TradeOrder order) {
        //1.校验订单是否为空
        if (order == null) {
            CastException.cast(ShopCode.SHOP_ORDER_INVALID);
        }
        //2.检验订单商品是否存在
        Long goodsId = order.getGoodsId();
        TradeGoods goods = goodsService.findOne(goodsId);
        if (goods == null) {
            CastException.cast(ShopCode.SHOP_GOODS_NO_EXIST);
        }
        //3.校验下单用户是否存在
        TradeUser user = userService.findOne(order.getUserId());
        if (user == null) {
            CastException.cast(ShopCode.SHOP_USER_NO_EXIST);
        }
        //4.校验订单金额是否合法,判断订金额是否等于单价乘以订单数量
        if (order.getPayAmount().compareTo(order.getGoodsPrice().multiply(new BigDecimal(order.getGoodsNumber()))) != 0) {
            CastException.cast(ShopCode.SHOP_ORDERAMOUNT_INVALID);
        }
        //5.校验订单商品数量是否合法: 订单数量小于等于当前库存数量
        if (order.getGoodsNumber() > goods.getGoodsNumber()) {
            CastException.cast(ShopCode.SHOP_GOODS_NUM_NOT_ENOUGH);
        }
        log.info("校验订单通过");
    }
}
