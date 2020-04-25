package com.zju.wsh.exception;

import com.zju.wsh.constant.ShopCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: zjumic
 * @Date: created at 20:45 2020/4/25
 */
@Slf4j
// 异常抛出类
public class CastException {
    public static void cast(ShopCode shopCode) {
        log.error(shopCode.toString());
        throw new CustomerException(shopCode);
    }
}
