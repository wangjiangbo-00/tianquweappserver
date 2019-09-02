package com.ziyoushenghuo.asyntask;




import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.entry.*;

import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.repository.GiftActivityRepository;
import com.ziyoushenghuo.repository.GiftParticipateRepository;
import com.ziyoushenghuo.service.CustomerService;

import com.ziyoushenghuo.weixintemplete.WeixinTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;



/* 抽奖预约消息发送
  @author 王江波
  @version V1.0
*/
@Component
public class GiftAppointmentTemplateMsg {

    private static Logger logger = LoggerFactory.getLogger(GiftAppointmentTemplateMsg.class);
    @Autowired
    GiftParticipateRepository giftParticipateRepository;

    @Autowired
    GiftActivityRepository giftActivityRepository;

    @Autowired
    CustomerService customerService;

    @Autowired
    private WeixinTemplate weixinTemplate;

    @Autowired
    private RedisUtils redisUtils;


    @Async
    public void sendAppointmentTempleMsg(int id) throws InterruptedException {

        try {

            GiftActivity giftActivity = giftActivityRepository.findById(id);

            if(giftActivity == null )
            {
                return;
            }

            List<GiftParticipate>giftParticipates = giftParticipateRepository.findAllByGiftidAndStatus(id,GiftParticipate.PARTICIPATE_ACTION_APPOINTMENT);


            for(GiftParticipate giftParticipate:giftParticipates)
            {
                try
                {
                    String access_token = (String) redisUtils.get(RedisKeyConstant.TOKEN_ZXTC_WEIXIN_VALUE);
                    Customer customer = customerService.GetOpenIdById(giftParticipate.getUserid());

                    if(customer!=null)
                    {
                        weixinTemplate.sendLotteryAppointment(access_token,giftActivity.getGift_name(),customer,id,giftParticipate.getFormid(),false);
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

        } catch (Exception e) {
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();

                logger.warn(e.getMessage().substring(0, len));
            } else {

                logger.warn(e.toString());
            }
        }
    }

}
