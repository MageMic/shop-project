package com.zju.wsh.mapper;

import com.zju.wsh.pojo.TradeCoupon;
import com.zju.wsh.pojo.TradeCouponExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TradeCouponMapper {
    int countByExample(TradeCouponExample example);

    int deleteByExample(TradeCouponExample example);

    int deleteByPrimaryKey(Long couponId);

    int insert(TradeCoupon record);

    int insertSelective(TradeCoupon record);

    List<TradeCoupon> selectByExample(TradeCouponExample example);

    TradeCoupon selectByPrimaryKey(Long couponId);

    int updateByExampleSelective(@Param("record") TradeCoupon record, @Param("example") TradeCouponExample example);

    int updateByExample(@Param("record") TradeCoupon record, @Param("example") TradeCouponExample example);

    int updateByPrimaryKeySelective(TradeCoupon record);

    int updateByPrimaryKey(TradeCoupon record);
}