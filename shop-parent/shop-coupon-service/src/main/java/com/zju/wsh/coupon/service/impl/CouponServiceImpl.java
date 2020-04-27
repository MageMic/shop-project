package com.zju.wsh.coupon.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zju.wsh.api.ICouponService;
import com.zju.wsh.constant.ShopCode;
import com.zju.wsh.entity.Result;
import com.zju.wsh.exception.CastException;
import com.zju.wsh.pojo.TradeCoupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.zju.wsh.coupon.mapper.TradeCouponMapper;
/**
 * @Author: zjumic
 * @Date: created at 22:26 2020/4/25
 */
@Component
@Service(interfaceClass = ICouponService.class)
public class CouponServiceImpl implements ICouponService {

    @Autowired
    private TradeCouponMapper tradeCouponMapper;

    @Override
    public Result updateCouponStatus(TradeCoupon coupon) {
        if(coupon==null||coupon.getCouponId()==null){
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        //更新优惠券状态
        tradeCouponMapper.updateByPrimaryKey(coupon);
        return new Result(ShopCode.SHOP_SUCCESS.getSuccess(),ShopCode.SHOP_SUCCESS.getMessage());
    }

    @Override
    public TradeCoupon findOne(Long couponId) {
        if (couponId == null) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        return tradeCouponMapper.selectByPrimaryKey(couponId);
    }
}
