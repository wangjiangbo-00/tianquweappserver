package com.ziyoushenghuo.rabbitmq.schedule;

import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.entry.GiftActivity;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.entry.PromotionDiscount;
import com.ziyoushenghuo.rabbitmq.MQConstant;
import com.ziyoushenghuo.rabbitmq.delay.DelayAfterReceiveSender;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.repository.GiftActivityRepository;
import com.ziyoushenghuo.repository.PromotionDiscountGoodsRepository;
import com.ziyoushenghuo.repository.PromotionDiscountRepository;
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

import javax.persistence.criteria.CriteriaBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@RabbitListener(queues = MQConstant.SCHEDULE_DISCOUNT_SHOW)
public class DiscountActivityShow {

    @Autowired
    PromotionDiscountRepository promotionDiscountRepository;

    @Autowired
    PromotionDiscountGoodsRepository promotionDiscountGoodsRepository;

    @Autowired
    RedisUtils redisUtils;

    private static Logger logger = LoggerFactory.getLogger(DiscountActivityShow.class);

    @RabbitHandler
    public void process(String id) {

        try {
            logger.warn("deal with discount show with id = " + id);
            int gid = Integer.valueOf(id);

            PromotionDiscount promotionDiscount = promotionDiscountRepository.findById(gid);

            if(promotionDiscount!=null)
            {

                if(promotionDiscount.getType() == PromotionDiscount.RPOMOTION_TYPE_SHOP)
                {
                    promotionDiscount.setStatus(PromotionDiscount.STATUS_CAN_SHOW);
                    promotionDiscountRepository.save(promotionDiscount);
                    promotionDiscountGoodsRepository.updateAllStatusByDiscountId(PromotionDiscount.STATUS_CAN_SHOW,gid);
                }
                else if(promotionDiscount.getType() == PromotionDiscount.RPOMOTION_TYPE_LONG)
                {
                    promotionDiscount.setStatus(PromotionDiscount.STATUS_CAN_SHOW);
                    promotionDiscountRepository.save(promotionDiscount);
                    promotionDiscountGoodsRepository.updateAllStatusByDiscountId(PromotionDiscount.STATUS_CAN_SHOW,gid);


                    List<Object> items = new ArrayList<>();

                    items.add(id);
                    redisUtils.hmDelete(RedisKeyConstant.VALUE_GET_PLATFORM_DISCOUNT_GOODS,items.toArray());
                }
                else if(promotionDiscount.getType() == PromotionDiscount.RPOMOTION_TYPE_LIMIT)
                {
                    promotionDiscount.setStatus(PromotionDiscount.STATUS_CAN_SHOW);
                    promotionDiscountRepository.save(promotionDiscount);
                    promotionDiscountRepository.updateAllRelatedDiscount(PromotionDiscount.STATUS_CAN_SHOW,gid);
                    promotionDiscountGoodsRepository.updateAllStatusByPid(PromotionDiscount.STATUS_CAN_SHOW,gid);


                    List<Object> items = new ArrayList<>();

                    items.add(id);
                    redisUtils.hmDelete(RedisKeyConstant.VALUE_GET_PLATFORM_DISCOUNT_GOODS,items.toArray());
                }




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
