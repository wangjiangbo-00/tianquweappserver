package com.ziyoushenghuo.rabbitmq.schedule;

import com.ziyoushenghuo.asyntask.GiftAppointmentTemplateMsg;
import com.ziyoushenghuo.asyntask.LotteryQrTask;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.common.QiniuHelper;
import com.ziyoushenghuo.common.WeixinHelper;
import com.ziyoushenghuo.entry.CustomerRecommendImg;
import com.ziyoushenghuo.entry.GiftActivity;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.rabbitmq.MQConstant;
import com.ziyoushenghuo.rabbitmq.delay.DelayAfterReceiveSender;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.repository.GiftActivityRepository;
import com.ziyoushenghuo.response.ResponeCodeConstant;
import com.ziyoushenghuo.scheduling.FlagCheckOrderExpress;
import com.ziyoushenghuo.service.OrderService;
import com.ziyoushenghuo.service.ShopProfitService;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.persistence.criteria.CriteriaBuilder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@RabbitListener(queues = MQConstant.SCHEDULE_GIFT_START)
public class GiftActivityStart {

    @Autowired
    GiftActivityRepository giftActivityRepository;


    @Autowired
    RedisUtils redisUtils;

    @Autowired
    GiftAppointmentTemplateMsg giftAppointmentTemplateMsg;

    @Autowired
    LotteryQrTask lotteryQrTask;

    private static Logger logger = LoggerFactory.getLogger(GiftActivityStart.class);

    @RabbitHandler
    public void process(String id) {

        try {
            logger.warn("deal with gift activity start with id = " + id);
            int gid = Integer.valueOf(id);

            GiftActivity giftActivity = giftActivityRepository.findById(gid);

            if(giftActivity!=null)
            {
                giftActivity.setStatus(GiftActivity.GIFT_STATUS_START);

                if(giftActivity.getQrpic()==null || giftActivity.getQrpic().isEmpty())
                {
                    lotteryQrTask.generateQr(giftActivity);
                }



                giftActivityRepository.save(giftActivity);

                if(giftActivity.getGiftfrom() == GiftActivity.GIFT_FROM_SHOP)
                {
                    List<Object> items = new ArrayList<>();

                    items.add(id);
                    redisUtils.remove(RedisKeyConstant.VALUE_GET_PLATFORM_LOTTERY);
                    redisUtils.hmDelete(RedisKeyConstant.VALUE_GET_LOTTERY_DETAILS,items.toArray());


                    redisUtils.remove(RedisKeyConstant.VALUE_GIFT_ACTIVITY_PERSONAS_PREX + id);
                    redisUtils.hmSet(RedisKeyConstant.VALUE_GIFT_ACTIVITY_PEOPLE,id,"0");
                }



                //todo generate qr pic



                giftAppointmentTemplateMsg.sendAppointmentTempleMsg(giftActivity.getId());

            }
        }catch (Exception e)
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
