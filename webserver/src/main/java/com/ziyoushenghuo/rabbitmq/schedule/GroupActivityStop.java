package com.ziyoushenghuo.rabbitmq.schedule;

import com.ziyoushenghuo.alisms.AliSmsUtils;
import com.ziyoushenghuo.asyntask.SuperGroupEndTask;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.entry.GiftActivity;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.entry.Refund;
import com.ziyoushenghuo.entry.TeamFounder;
import com.ziyoushenghuo.rabbitmq.MQConstant;
import com.ziyoushenghuo.rabbitmq.delay.DelayAfterReceiveSender;
import com.ziyoushenghuo.rabbitmq.delay.DelayRefundFailSender;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.repository.GiftActivityRepository;
import com.ziyoushenghuo.repository.TeamFounderRepository;
import com.ziyoushenghuo.scheduling.FlagCheckOrderExpress;
import com.ziyoushenghuo.service.OrderService;
import com.ziyoushenghuo.service.RefundService;
import com.ziyoushenghuo.service.ShopProfitService;
import com.ziyoushenghuo.service.TeamFounderService;
import com.ziyoushenghuo.weixinpay.WxPayUtils;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.sun.tools.internal.xjc.reader.gbind.Expression.EPSILON;
import static org.apache.coyote.http11.Constants.a;

@Component
@RabbitListener(queues = MQConstant.SCHEDULE_GROUP_STOP)
public class GroupActivityStop {

    @Autowired
    SuperGroupEndTask superGroupEndTask;

    @Autowired
    RedisUtils redisUtils;

    private static Logger logger = LoggerFactory.getLogger(GroupActivityStop.class);

    @RabbitHandler
    public void process(String id) {

        try {
            logger.warn("deal with group stop with id = " + id);
            int gid = Integer.valueOf(id);

            superGroupEndTask.groupEndTask(gid);

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
