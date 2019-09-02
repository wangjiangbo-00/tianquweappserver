package com.ziyoushenghuo.rabbitmq.schedule;

import com.ziyoushenghuo.asyntask.GiftAppointmentTemplateMsg;
import com.ziyoushenghuo.asyntask.GroupAppointmentTemplateMsg;
import com.ziyoushenghuo.asyntask.SuperGroupQrTask;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.common.QiniuHelper;
import com.ziyoushenghuo.common.WeixinHelper;
import com.ziyoushenghuo.entry.GiftActivity;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.entry.TeamFounder;
import com.ziyoushenghuo.rabbitmq.MQConstant;
import com.ziyoushenghuo.rabbitmq.delay.DelayAfterReceiveSender;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.repository.GiftActivityRepository;
import com.ziyoushenghuo.repository.TeamFounderRepository;
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
@RabbitListener(queues = MQConstant.SCHEDULE_GROUP_START)
public class GroupActivityStart {

    @Autowired
    TeamFounderRepository teamFounderRepository;


    @Autowired
    GroupAppointmentTemplateMsg groupAppointmentTemplateMsg;

    @Autowired
    RedisUtils redisUtils;


    @Autowired
    SuperGroupQrTask superGroupQrTask;

    private static Logger logger = LoggerFactory.getLogger(GroupActivityStart.class);




    @RabbitHandler
    public void process(String id) {

        try {
            logger.warn("deal with group start with id = " + id);
            int gid = Integer.valueOf(id);

            TeamFounder teamFounder = teamFounderRepository.findById(gid);

            if(teamFounder!=null)
            {
                teamFounder.setStatus(TeamFounder.GROUP_STATUS_START);

                teamFounderRepository.save(teamFounder);

                if(teamFounder.getQrpic()==null || teamFounder.getQrpic().isEmpty())
                {
                    superGroupQrTask.generateQr(teamFounder);
                }

                List<Object> items = new ArrayList<>();

                items.add(id);

                redisUtils.remove(RedisKeyConstant.VALUE_GET_PLATFORM_GROUP);
                redisUtils.hmDelete(RedisKeyConstant.VALUE_GET_PLATFROMGROUP_DETAILS,items.toArray());

                groupAppointmentTemplateMsg.sendAppointmentTempleMsg(teamFounder);
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
