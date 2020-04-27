package com.zju.wsh.test;

import com.zju.wsh.api.IOrderService;
import com.zju.wsh.order.OrderServiceApplication;
import com.zju.wsh.pojo.TradeOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @Author: zjumic
 * @Date: created at 23:53 2020/4/26
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderServiceApplication.class)
public class OrderTest {

    @Autowired
    private IOrderService orderService;

    @Test
    public void confirmOrder() throws IOException {

        Long coupouId = 345988230098857984L;
        Long goodsId = 345959443973935104L;
        Long userId = 345963634385633280L;

        TradeOrder order = new TradeOrder();
        order.setGoodsId(goodsId);
        order.setUserId(userId);
//        order.setCouponId(coupouId);
        order.setAddress("北京");
        order.setGoodsNumber(1);
        order.setGoodsPrice(new BigDecimal(1000));
        order.setShippingFee(BigDecimal.ZERO);
        order.setOrderAmount(new BigDecimal(1000));
        order.setMoneyPaid(new BigDecimal(100));
        orderService.confirmOrder(order);

        System.in.read();
    }
}
