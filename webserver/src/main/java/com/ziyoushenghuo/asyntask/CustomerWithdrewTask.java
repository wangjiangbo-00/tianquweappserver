package com.ziyoushenghuo.asyntask;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.common.WeixinHelper;
import com.ziyoushenghuo.entry.*;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.repository.CustomerAccountRecordRepository;
import com.ziyoushenghuo.repository.CustomerAccountRepository;
import com.ziyoushenghuo.repository.CustomerWithdrewRepository;
import com.ziyoushenghuo.repository.GiftActivityRepository;
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
public class CustomerWithdrewTask {

    private static Logger logger = LoggerFactory.getLogger(CustomerWithdrewTask.class);
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    CustomerWithdrewRepository customerWithdrewRepository;


    @Autowired
    CustomerAccountRepository customerAccountRepository;

    @Autowired
    CustomerAccountRecordRepository customerAccountRecordRepository;
    @Async
    public void withdrew(CustomerWithdrew customerWithdrew, CustomerAccount customerAccount, CustomerAccountRecord customerAccountRecord) throws InterruptedException{

        try {
            WxPayUtils.RefundResult refundResult = WxPayUtils.customerwithdrew(customerWithdrew);

            if(refundResult.getResult() == WxPayUtils.ORDER_RESULT_OK)
            {
                customerWithdrew.setStatus(CustomerWithdrew.STATUS_OK);
                customerWithdrew.setWithdrewtime(new Date());

                customerWithdrewRepository.save(customerWithdrew);


                customerAccount.setMoney_lock(0);

                customerAccount.setWithdraw(customerAccount.getWithdraw() + customerWithdrew.getMoney());

                customerAccountRepository.save(customerAccount);



                customerAccountRecord.setRemark("提现成功");
                customerAccountRecord.setStatus(CustomerAccountRecord.OP_PROFIT_STATUS_OK);

                customerAccountRecordRepository.save(customerAccountRecord);

            }
            else
            {
                customerWithdrew.setStatus(CustomerWithdrew.STATUS_FAIL);

                customerWithdrew.setFailstr(refundResult.getErrdes());
                customerWithdrew.setErrcode(refundResult.getErrcode());
                customerWithdrewRepository.save(customerWithdrew);

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
