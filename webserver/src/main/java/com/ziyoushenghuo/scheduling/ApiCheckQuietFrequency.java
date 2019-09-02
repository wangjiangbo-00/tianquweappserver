package com.ziyoushenghuo.scheduling;



import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.entry.Refund;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.service.OrderService;
import com.ziyoushenghuo.web.controller.SystemConfigController;
import com.ziyoushenghuo.weixintoken.WxTokenResult;
import com.ziyoushenghuo.weixintoken.WxTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

/* 这里处理10分钟和60分钟事件，主要是定时刷新一些缓存，避免逻辑遗漏
  @author 王江波
  @version V1.0
*/
@Component
public class ApiCheckQuietFrequency {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private OrderService orderService;



    private static Logger logger = LoggerFactory.getLogger(ApiCheckQuietFrequency.class);

    @Scheduled(initialDelay = SchedulingConstant.COMMON_DELAY,fixedRate = SchedulingConstant.API_CHECK_QUIET_FREQUENCY)
    public void task()
    {
        checkForPlatform10MinValue();
        checkForPlatform60MinValue();

    }



    private void  checkForPlatform10MinValue()
    {

        try {
            String api_get_GoodsCategorys = redisTemplate.opsForValue().get(RedisKeyConstant.FLAG_PLATFORM_COMMON_10_MIN);
            if(api_get_GoodsCategorys==null)
            {
                redisUtils.set(RedisKeyConstant.FLAG_PLATFORM_COMMON_10_MIN,"1",(long)SchedulingConstant.FLAG_COMMON_10_MIN,TimeUnit.MINUTES);
                redisUtils.remove(RedisKeyConstant.VALUE_GET_PLATFORM_LOTTERY);
                redisUtils.remove(RedisKeyConstant.VALUE_GET_PLATFORM_GROUP);
                redisUtils.remove(RedisKeyConstant.VALUE_GET_PLATFORM_ACTIVITIES);
                redisUtils.remove(RedisKeyConstant.VALUE_GET_BANNERS);
                redisUtils.remove(RedisKeyConstant.VALUE_GET_PLATFORM_DISCOUNT_GOODS);
                redisUtils.remove(RedisKeyConstant.VALUE_GET_PROMITION_SHOWS);


                redisUtils.remove(RedisKeyConstant.VALUE_SYS_SEARCH_GOODS);
                redisUtils.remove(RedisKeyConstant.VALUE_GET_GOODS_DETAILS_V1);

                redisUtils.remove(RedisKeyConstant.VALUE_GET_RECOMMEND_SHOPS);
                redisUtils.remove(RedisKeyConstant.VALUE_GET_RECOMMEND_GOODS);

                redisUtils.remove(RedisKeyConstant.VALUE_GET_SHOP_GOODS);
            }
        }catch (Exception e)
        {
            logger.warn(e.getMessage());
        }

    }

    private void  checkForPlatform60MinValue()
    {

        try {
            String api_get_GoodsCategorys = redisTemplate.opsForValue().get(RedisKeyConstant.FLAG_PLATFORM_COMMON_60_MIN);
            if(api_get_GoodsCategorys==null)
            {
                redisUtils.set(RedisKeyConstant.FLAG_PLATFORM_COMMON_60_MIN,"1",(long)SchedulingConstant.FLAG_COMMON_60_MIN,TimeUnit.MINUTES);

                redisUtils.remove(RedisKeyConstant.VALUE_GET_CATEGORYS);
                redisUtils.remove(RedisKeyConstant.VALUE_SYS_REGIONS);
                redisUtils.remove(RedisKeyConstant.VALUE_SYS_QUESTIONS);
                redisUtils.remove(RedisKeyConstant.VALUE_SYS_SHOP_PROVINCES);

                redisUtils.remove(RedisKeyConstant.VALUE_GET_SHOP_FEELIST);
                redisUtils.remove(RedisKeyConstant.VALUE_SUPERGROUP_PEOPLE);
                redisUtils.remove(RedisKeyConstant.VALUE_GET_PLATFROMGROUP_DETAILS);
                redisUtils.remove(RedisKeyConstant.VALUE_GET_SHOP_DETAILS);
                redisUtils.remove(RedisKeyConstant.VALUE_GET_LOTTERY_DETAILS);

                redisUtils.remove(RedisKeyConstant.VALUE_GET_FREEORDERS);



            }
        }catch (Exception e)
        {
            logger.warn(e.getMessage());
        }

    }





}
