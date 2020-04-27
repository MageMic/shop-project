package com.zju.wsh.api;

import com.zju.wsh.entity.Result;
import com.zju.wsh.pojo.TradeCoupon;

/**
 * @Author: zjumic
 * @Date: created at 22:23 2020/4/25
 */
public interface ICouponService {

    TradeCoupon findOne(Long couponId);

    Result updateCouponStatus(TradeCoupon coupon);
}
