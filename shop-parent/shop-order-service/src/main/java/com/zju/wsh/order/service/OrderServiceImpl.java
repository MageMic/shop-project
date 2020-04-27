package com.zju.wsh.order.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.zju.wsh.api.ICouponService;
import com.zju.wsh.api.IGoodsService;
import com.zju.wsh.api.IOrderService;
import com.zju.wsh.api.IUserService;
import com.zju.wsh.constant.ShopCode;
import com.zju.wsh.entity.MQEntity;
import com.zju.wsh.entity.Result;
import com.zju.wsh.exception.CastException;
import com.zju.wsh.order.mapper.TradeOrderMapper;
import com.zju.wsh.pojo.*;
import com.zju.wsh.utils.IDWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

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
    @Reference
    private ICouponService couponService;


    @Value("${mq.order.topic}")
    private String topic;

    @Value("${mq.order.tag.cancel}")
    private String tag;


    @Autowired
    private IDWorker idWorker;

    @Autowired
    private TradeOrderMapper orderMapper;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public Result confirmOrder(TradeOrder order) {
        //1.校验订单
        checkOrder(order);
        //2.生成预订单
        Long orderId = savePreOrder(order);
        try {
            //3.扣减库存
            reduceGoodsNum(order);
            //4.扣减优惠券
            updateCouponStatus(order);
            //5.使用余额
            reduceMoneyPaid(order);
            //6.确认订单
            updateOrderStatus(order);
            //7.返回成功状态
            return new Result(ShopCode.SHOP_SUCCESS.getSuccess(),ShopCode.SHOP_SUCCESS.getMessage());
        } catch (Exception e) {
            //1.确认订单失败,发送消息
            MQEntity mqEntity = new MQEntity();
            mqEntity.setOrderId(orderId);
            mqEntity.setUserId(order.getUserId());
            mqEntity.setUserMoney(order.getMoneyPaid());
            mqEntity.setGoodsId(order.getGoodsId());
            mqEntity.setGoodsNum(order.getGoodsNumber());
            mqEntity.setCouponId(order.getCouponId());
            //2.返回失败状态
            try {
                sendCancelOrder(topic,tag,order.getOrderId().toString(), JSON.toJSONString(mqEntity));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return new Result(ShopCode.SHOP_FAIL.getSuccess(),ShopCode.SHOP_FAIL.getMessage());
        }
    }

    /**
     *发送订单确认失败消息
     * @param topic
     * @param tag
     * @param keys
     * @param body
     */
    private void sendCancelOrder(String topic, String tag, String keys, String body) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        Message message = new Message(topic,tag,keys,body.getBytes());
        rocketMQTemplate.getProducer().send(message);
    }

    /**
     * 确认订单
     * @param order
     */
    private void updateOrderStatus(TradeOrder order) {
        order.setOrderStatus(ShopCode.SHOP_ORDER_CONFIRM.getCode());
        order.setPayStatus(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode());
        order.setConfirmTime(new Date());
        int r = orderMapper.updateByPrimaryKey(order);
        // 返回值是是否成功的码
        if(r <= 0){
            CastException.cast(ShopCode.SHOP_ORDER_CONFIRM_FAIL);
        }
        log.info("订单:"+order.getOrderId()+"确认订单成功");
    }

    /**
     * 扣减余额
     * @param order
     */
    private void reduceMoneyPaid(TradeOrder order) {
        if (order.getMoneyPaid() != null && order.getMoneyPaid().compareTo(BigDecimal.ZERO) > 0) {
            TradeUserMoneyLog userMoneyLog = new TradeUserMoneyLog();
            userMoneyLog.setOrderId(order.getOrderId());
            userMoneyLog.setUserId(order.getUserId());
            userMoneyLog.setUseMoney(order.getMoneyPaid());
            userMoneyLog.setMoneyLogType(ShopCode.SHOP_USER_MONEY_PAID.getCode());
            Result result = userService.updateMoneyPaid(userMoneyLog);
            if(result.getSuccess().equals(ShopCode.SHOP_FAIL.getSuccess())){
                CastException.cast(ShopCode.SHOP_USER_MONEY_REDUCE_FAIL);
            }
            log.info("订单:"+order.getOrderId()+",扣减余额成功");
        }
    }

    /**
     * 使用优惠券
     * @param order
     */
    private void updateCouponStatus(TradeOrder order) {
        if (order.getCouponId() != null) {
            TradeCoupon coupon = couponService.findOne(order.getCouponId());
            coupon.setOrderId(order.getOrderId());
            coupon.setIsUsed(ShopCode.SHOP_COUPON_ISUSED.getCode());
            coupon.setUsedTime(new Date());

            //更新优惠券状态
            Result result =  couponService.updateCouponStatus(coupon);
            if(result.getSuccess().equals(ShopCode.SHOP_FAIL.getSuccess())){
                CastException.cast(ShopCode.SHOP_COUPON_USE_FAIL);
            }
            log.info("订单:"+order.getOrderId()+",使用优惠券");
        }
    }

    /**
     * 扣减库存
     * @param order
     */
    private void reduceGoodsNum(TradeOrder order) {
        // 订单id 商品id 商品数量
        TradeGoodsNumberLog goodsNumberLog = new TradeGoodsNumberLog();
        goodsNumberLog.setOrderId(order.getOrderId());
        goodsNumberLog.setGoodsId(order.getGoodsId());
        goodsNumberLog.setGoodsNumber(order.getGoodsNumber());

        Result result = goodsService.reduceGoodsNum(goodsNumberLog);
        if (result.getSuccess().equals(ShopCode.SHOP_FAIL.getSuccess())) {
            CastException.cast(ShopCode.SHOP_REDUCE_GOODS_NUM_FAIL);
        }
        log.info("订单:{}扣减库存成功", order.getOrderId());
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
        //4.校验订单单价是否合法
        if (order.getGoodsPrice().compareTo(goods.getGoodsPrice()) != 0) {
            CastException.cast(ShopCode.SHOP_GOODS_PRICE_INVALID);
        }
        //5.校验订单商品数量是否合法: 订单数量小于等于当前库存数量
        if (order.getGoodsNumber() > goods.getGoodsNumber()) {
            CastException.cast(ShopCode.SHOP_GOODS_NUM_NOT_ENOUGH);
        }
        log.info("校验订单通过");
    }

    /**
     * 生成预订单
     * @param order
     */
    private Long savePreOrder(TradeOrder order) {
        //1.设置订单状态为不可见
        order.setOrderStatus(ShopCode.SHOP_ORDER_NO_CONFIRM.getCode());
        //2.设置订单ID
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);
        //3.核算订单运费
        BigDecimal shippingFee = calculateShippingFee(order.getOrderAmount());
        if (shippingFee.compareTo(order.getShippingFee()) != 0) {
            CastException.cast(ShopCode.SHOP_ORDER_SHIPPINGFEE_INVALID);
        }
        //4.计算订单总价格是否正确
        BigDecimal orderAmount = order.getGoodsPrice().multiply(new BigDecimal(order.getGoodsNumber()));
        orderAmount.add(shippingFee);
        if (order.getOrderAmount().compareTo(orderAmount) != 0) {
            CastException.cast(ShopCode.SHOP_ORDERAMOUNT_INVALID);
        }
        //5.判断用户是否使用余额
        BigDecimal moneyPaid = order.getMoneyPaid();
        if(moneyPaid!=null){
            //5.1 订单中余额是否合法
            int r = moneyPaid.compareTo(BigDecimal.ZERO);
            //余额小于0
            if(r < 0){
                CastException.cast(ShopCode.SHOP_MONEY_PAID_LESS_ZERO);
            }
            //余额大于0
            if(r > 0){
                TradeUser user = userService.findOne(order.getUserId());
                // 超过数据库里的余额
                if(moneyPaid.compareTo(new BigDecimal(user.getUserMoney())) > 0){
                    CastException.cast(ShopCode.SHOP_MONEY_PAID_INVALID);
                }
            }
        }else{
            order.setMoneyPaid(BigDecimal.ZERO);
        }
        //6.判断是否使用优惠券
        Long couponId = order.getCouponId();
        if (couponId != null) {
            // 判断优惠券是否存在
            TradeCoupon coupon = couponService.findOne(couponId);
            if (coupon == null) {
                CastException.cast(ShopCode.SHOP_COUPON_NO_EXIST);
            }
            // 判断优惠券是否被使用
            if (coupon.getIsUsed() == ShopCode.SHOP_COUPON_ISUSED.getCode().intValue()) {
                CastException.cast(ShopCode.SHOP_COUPON_ISUSED);
            }
            order.setCouponPaid(coupon.getCouponPrice());
        } else {
            order.setCouponPaid(BigDecimal.ZERO);
        }
        //7.核算订单支付金额：订单总金额-余额-优惠券金额
        BigDecimal payAmount = order.getOrderAmount().subtract(order.getMoneyPaid()).subtract(order.getCouponPaid());
        order.setPayAmount(payAmount);
        //8.设置下单时间
        order.setAddTime(new Date());
        //9.保存订单到数据库
        orderMapper.insert(order);
        //10.返回订单ID
        return orderId;
    }

    /**
     * 核算运费
     * @param orderAmount
     * @return
     */
    private BigDecimal calculateShippingFee(BigDecimal orderAmount) {
        if (orderAmount.compareTo(new BigDecimal(100)) >= 0) {
            return BigDecimal.ZERO;
        } else {
            return BigDecimal.TEN;
        }
    }
}
