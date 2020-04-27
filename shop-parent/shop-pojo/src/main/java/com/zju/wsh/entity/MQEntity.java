package com.zju.wsh.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: zjumic
 * @Date: created at 16:29 2020/4/27
 */
@Data
public class MQEntity {
    private Long orderId;
    private Long couponId;
    private Long userId;
    private BigDecimal userMoney;
    private Long goodsId;
    private Integer goodsNum;
}
