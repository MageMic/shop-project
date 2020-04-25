package com.zju.wsh.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zjumic
 * @Date: created at 20:47 2020/4/25
 */

// 统一结果类
@Data
@AllArgsConstructor
public class Result implements Serializable {

    private Boolean success;
    private String message;

    public Result() {
    }

    @Override
    public String toString() {
        return "Result{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
