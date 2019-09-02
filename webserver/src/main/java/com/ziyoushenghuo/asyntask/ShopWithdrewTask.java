package com.ziyoushenghuo.asyntask;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.common.WeixinHelper;
import com.ziyoushenghuo.entry.*;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.repository.*;
import com.ziyoushenghuo.weixinoss.WxOssUtils;
import com.ziyoushenghuo.weixinpay.WxPayUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.util.Date;


/* 生成抽奖的二维码(小程序码)
   用于非推广员非主力抽奖使用
  @author 王江波
  @version V1.0
*/

@Component
public class ShopWithdrewTask {

    private static Logger logger = LoggerFactory.getLogger(ShopWithdrewTask.class);
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    WithdrewRepository withdrewRepository;


    @Autowired
    ShopAccountRepository shopAccountRepository;

    @Autowired
    ShopAccountRecordRepository shopAccountRecordRepository;
    @Async
    public void withdrew(Withdrew withdrew, ShopAccount shopAccount, ShopAccountRecord shopAccountRecord) throws InterruptedException{

        try {
            WxPayUtils.RefundResult refundResult = WxPayUtils.withdrew(withdrew);

            if(refundResult.getResult() == WxPayUtils.ORDER_RESULT_OK)
            {
                withdrew.setStatus(CustomerWithdrew.STATUS_OK);
                withdrew.setWithdrewtime(new Date());
                withdrew.setWxpayid(refundResult.getTransaction_id());

                withdrewRepository.save(withdrew);


                shopAccount.setShop_total_money_lock(0);

                shopAccount.setShop_withdraw(shopAccount.getShop_withdraw() + withdrew.getMoney());

                shopAccountRepository.save(shopAccount);



                shopAccountRecord.setRemark("提现成功");
                shopAccountRecord.setStatus(CustomerAccountRecord.OP_PROFIT_STATUS_OK);

                shopAccountRecordRepository.save(shopAccountRecord);

            }
            else
            {
                withdrew.setStatus(CustomerWithdrew.STATUS_FAIL);

                withdrew.setFailstr(refundResult.getErrdes());
                withdrew.setErrcode(refundResult.getErrcode());
                withdrewRepository.save(withdrew);

                // 失败的退款手动处理


            }

        }
        catch (Exception e)
        {
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();

                logger.warn(e.getMessage().substring(0, len));
            } else {

                logger.warn(e.toString());
            }

        }
    }

}
