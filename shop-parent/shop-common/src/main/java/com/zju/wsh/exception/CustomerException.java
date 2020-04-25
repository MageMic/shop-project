package com.zju.wsh.exception;

import com.zju.wsh.constant.ShopCode;

/**
 * @Author: zjumic
 * @Date: created at 20:41 2020/4/25
 */
public class CustomerException extends RuntimeException {

    private ShopCode shopCode;

    public CustomerException(ShopCode shopCode) {
        this.shopCode = shopCode;
    }
}
