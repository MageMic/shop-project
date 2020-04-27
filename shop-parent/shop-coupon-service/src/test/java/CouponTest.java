import com.zju.wsh.api.ICouponService;
import com.zju.wsh.coupon.CouponServiceApplication;
import com.zju.wsh.pojo.TradeCoupon;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: zjumic
 * @Date: created at 1:16 2020/4/27
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CouponServiceApplication.class)
public class CouponTest {

    @Autowired
    ICouponService couponService;

    @Test
    public void check() {
        Long coupouId = 345988230098857984L;
        TradeCoupon coupon = couponService.findOne(coupouId);
        if (coupon != null) {
            System.out.println(coupon.getCouponPrice());
        }

    }
}
