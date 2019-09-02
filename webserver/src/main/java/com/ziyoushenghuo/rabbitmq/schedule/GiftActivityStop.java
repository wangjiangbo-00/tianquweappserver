package com.ziyoushenghuo.rabbitmq.schedule;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.common.Md5Util;
import com.ziyoushenghuo.entry.*;
import com.ziyoushenghuo.rabbitmq.MQConstant;
import com.ziyoushenghuo.rabbitmq.delay.DelayAfterReceiveSender;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.repository.*;
import com.ziyoushenghuo.scheduling.FlagCheckOrderExpress;
import com.ziyoushenghuo.service.OrderService;
import com.ziyoushenghuo.service.ShopProfitService;
import com.ziyoushenghuo.weixintemplete.WeixinTemplate;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@RabbitListener(queues = MQConstant.SCHEDULE_GIFT_STOP)
public class GiftActivityStop {

    @Autowired
    GiftActivityRepository giftActivityRepository;

    @Autowired
    GiftParticipateRepository giftParticipateRepository;


    @Autowired
    GiftWinnerRepository giftWinnerRepository;

    @Autowired
    CustomerRepository customerRepository;


    @Autowired
    OrderGoodsRepository orderGoodsRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    WeixinTemplate weixinTemplate;


    @Autowired
    ShopRepository shopRepository;


    @Autowired
    OrderShopRepository orderShopRepository;

    @Autowired
    OrderExtraRepository orderExtraRepository;

    private static Logger logger = LoggerFactory.getLogger(GiftActivityStop.class);


    @RabbitHandler
    public void process(String id) {

        try {
            logger.warn("deal with gift   activity stop with id = " + id);
            int gid = Integer.valueOf(id);

            GiftActivity giftActivity = giftActivityRepository.findById(gid);

            if(giftActivity!=null)
            {

                int mode = giftActivity.getMode();
                if(mode == GiftActivity.GIFT_MODE_TIME_DUE || mode == GiftActivity.GIFT_MODE_PEOPLE_COUNT  || mode == GiftActivity.GIFT_MODE_OWNER_OPEN)
                {

                    if(giftActivity.getStatus() == GiftActivity.GIFT_STATUS_END)
                    {
                        logger.warn("deal with gift activity stop but with status already stop with id  = " + id);
                        return;
                    }

                    giftActivity.setStatus(GiftActivity.GIFT_STATUS_END);

                    giftActivityRepository.save(giftActivity);

                    int num = giftActivity.getNum();

                    List<Integer> participates = giftParticipateRepository.findAllGiftIds(giftActivity.getId(),GiftParticipate.PARTICIPATE_ACTION_ALREADY_IN);


                    if(!participates.isEmpty())
                    {
                        int count = giftActivity.getNum();

                        List<Integer> prizes = AppCommon.getRandomSubList(participates,count);


                        List<GiftParticipate> giftParticipates = giftParticipateRepository.findAllByIdIn(prizes);



                        List<Integer> customerids = new ArrayList<>(prizes.size());

                        JsonArray jsonArray = new JsonArray();


                        Shop shop = shopRepository.findById(giftActivity.getOwnerid());

                        for(GiftParticipate giftParticipate:giftParticipates)
                        {
                            Customer customer = customerRepository.findCoverAndOpenidById(giftParticipate.getUserid());

                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("uid",customer.getId());
                            jsonObject.addProperty("name",customer.getNickname());
                            jsonObject.addProperty("pic",customer.getHeadpic());


                            GiftWinner giftWinner = new GiftWinner();

                            giftWinner.setGiftid(gid);
                            giftWinner.setUserid(giftParticipate.getUserid());
                            giftWinner.setGiftname(giftActivity.getGift_name());
                            giftWinner.setWintime(new Date());

                            giftWinnerRepository.save(giftWinner);


                            if(giftActivity.getGiftfrom() == 0)
                            {
                                // no need create order
                                Order order = new Order();
                                StringBuilder sb = new StringBuilder("JW");
                                sb.append(customer.getId());

                                sb.append(new Date().getTime());
                                sb.append("-");
                                sb.append(0);
                                sb.append(giftActivity.getGoodsCover().getId());

                                order.setOuttradeno(sb.toString());
                                order.setBuyerid(0);
                                order.setShopId(giftActivity.getOwnerid());

                                order.setUsername(customer.getNickname());

                                order.setOrderStatus(Order.ORDER_STATUS_TOPAY);
                                order.setPayStatus(Order.ORDER_PAY_STATUS_NORMAL);
                                order.setProfitStatus(Order.ORDER_PROFIT_STATUS_NORMAL);

                                order.setOrdertype(Order.ORDER_TYPE_LOTTERY);


                                order.setCreatetime(new Date());


                                String token = Md5Util.getMD5(new Date().toString() + customer.getId());

                                order.setGiven_token(token);

                                orderService.Create(order);


                                OrderGoods orderGoods = new OrderGoods();
                                orderGoods.setId(order.getId());
                                orderGoods.setGoodId(giftActivity.getGoodsCover().getId());
                                orderGoods.setGoodtitle(giftActivity.getGoodsCover().getIntroduction());
                                orderGoods.setGoodsname(giftActivity.getGoodsCover().getGoods_name());

                                orderGoods.setGoodposter(giftActivity.getGoodsCover().getPictureurl());


                                orderGoods.setBuysum(1);
                                orderGoods.setGroup_number(1);
                                orderGoods.setGoods_price(giftActivity.getGoodsCover().getGroup_price());

                                orderGoods.setSkuid(0);
                                orderGoods.setSkuname("");

                                orderGoodsRepository.save(orderGoods);


                                if(shop!=null)
                                {
                                    OrderShop orderShop = new OrderShop();

                                    orderShop.setShopId(shop.getId());
                                    orderShop.setShopcontact(shop.getShopcontact());
                                    orderShop.setId(order.getId());

                                    orderShopRepository.save(orderShop);
                                }



                                OrderExtra orderExtra = new OrderExtra();
                                orderExtra.setBlessing_message("大吉大利，恭喜发财");
                                orderExtra.setBuyer_message("");
                                orderExtra.setId(order.getId());

                                if(shop!=null)
                                {
                                    orderExtra.setSendnickname(shop.getShopname());
                                    orderExtra.setSendheadpic(shop.getShoplogo());
                                }
                                else
                                {
                                    orderExtra.setSendnickname("田趣小集");
                                    orderExtra.setSendheadpic("");
                                }



                                orderExtraRepository.save(orderExtra);
                                jsonObject.addProperty("oid",order.getId());
                                jsonObject.addProperty("token",order.getGiven_token());

                            }
                            else
                            {
                                // no need create order
                                jsonObject.addProperty("oid","0");
                                jsonObject.addProperty("token","0");
                            }

                            jsonArray.add(jsonObject);



                            String access_token = (String) redisUtils.get(RedisKeyConstant.TOKEN_ZXTC_WEIXIN_VALUE);
                            weixinTemplate.sendLotteryResultNotify(access_token,giftActivity.getGift_name(),customer,Integer.valueOf(id),giftParticipate.getFormid(),true);
                        }

                        giftParticipateRepository.updatePrizeWinners(1,prizes);

                        giftActivity.setResult(jsonArray.toString());
                    }

                    giftActivityRepository.save(giftActivity);

                    //todo create order

                }
                else if(mode == GiftActivity.GIFT_MODE_RANDOM || mode == GiftActivity.GIFT_MODE_ASSIST   ){
                    //has nothing to do
                    giftActivity.setStatus(GiftActivity.GIFT_STATUS_END);

                    giftActivityRepository.save(giftActivity);
                }

                if(giftActivity.getGiftfrom() == GiftActivity.GIFT_FROM_SHOP) {

                    List<Object> items = new ArrayList<>();
                    items.add(id);
                    redisUtils.remove(RedisKeyConstant.VALUE_GET_PLATFORM_LOTTERY);
                    redisUtils.hmDelete(RedisKeyConstant.VALUE_GET_LOTTERY_DETAILS, items.toArray());
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
