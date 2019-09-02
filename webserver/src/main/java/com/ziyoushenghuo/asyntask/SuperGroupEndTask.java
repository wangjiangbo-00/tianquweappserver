package com.ziyoushenghuo.asyntask;

import com.ziyoushenghuo.alisms.AliSmsUtils;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.entry.Refund;
import com.ziyoushenghuo.entry.TeamFounder;
import com.ziyoushenghuo.rabbitmq.delay.DelayAfterDeliverySender;
import com.ziyoushenghuo.rabbitmq.delay.DelayRefundFailSender;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.repository.TeamFounderRepository;
import com.ziyoushenghuo.service.OrderService;
import com.ziyoushenghuo.service.RefundService;
import com.ziyoushenghuo.service.TeamFounderService;
import com.ziyoushenghuo.settlement.CustomerSettle;
import com.ziyoushenghuo.weixinpay.WxPayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


/* 超级团购结束，处理团购差价退款
  @author 王江波
  @version V1.0
*/
@Component
public class SuperGroupEndTask {

    private static Logger logger = LoggerFactory.getLogger(SuperGroupEndTask.class);
    @Autowired
    TeamFounderRepository teamFounderRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    TeamFounderService teamFounderService;

    @Autowired
    RefundService refundService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DelayRefundFailSender delayRefundFailSender;


    @Autowired
    private CustomerSettle customerSettle;

    @Autowired
    private DelayAfterDeliverySender delayAfterDeliverySender;


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Async
    public void groupEndTask(int groupid) throws InterruptedException {

        try {
            TeamFounder teamFounder = teamFounderRepository.findById(groupid);

            if (teamFounder != null && teamFounder.getStatus() == TeamFounder.GROUP_STATUS_START) {

                logger.info("deal with supergroup end task with id = " + groupid );

                teamFounder.setStatus(TeamFounder.GROUP_STATUS_END);
                teamFounderRepository.save(teamFounder);

                List<Object> items = new ArrayList<>();
                items.add(String.valueOf(groupid));
                redisUtils.remove(RedisKeyConstant.VALUE_GET_PLATFORM_GROUP);
                redisUtils.hmDelete(RedisKeyConstant.VALUE_GET_PLATFROMGROUP_DETAILS,items.toArray());

                // todo add redis status

                int people = teamFounder.getPeople();

                if(people>0)
                {
                    String formats = teamFounder.getStage_format();

                    String[] formatlist = formats.split(",");
                    double discount = 1.0;

                    boolean bfind = false;
                    if (formatlist.length > 0) {

                        if(people>=teamFounder.getRequirenum())
                        {
                            String format = formatlist[formatlist.length -1];
                            String[] formatvalue = format.split(":");
                            discount = Double.valueOf(formatvalue[1]);
                        }
                        else
                        {
                            for (int i=0;i<formatlist.length;i++) {

                                String format = formatlist[i];

                                String[] formatvalue = format.split(":");

                                int person = Integer.valueOf(formatvalue[0]);

                                if (person > people) {

                                    if(i==0)
                                    {
                                        discount = 10.0;
                                    }
                                    else
                                    {
                                        String formatbefore = formatlist[i-1];

                                        String[] formatvaluebefore = formatbefore.split(":");

                                        discount = Double.valueOf(formatvaluebefore[1]);
                                    }

                                    break;

                                }
                                else if(person == people)
                                {

                                    discount = Double.valueOf(formatvalue[1]);
                                    break;
                                }

                            }
                        }





                    }


                    discount = discount * 0.1; //计算时候按照

                    double refunddiscount = Math.abs(1.0 - discount) + AppCommon.EPSILON;


                    logger.info("deal with supergroup end task with id = " + groupid  + " and refunddiscount = " + refunddiscount);


                    List<Order> orders = orderService.getByGroupId(groupid);
                    for (Order order : orders) {
                        if (order.getPayStatus() != Order.ORDER_PAY_STATUS_NORMAL) {


                            if (Math.abs(1.0 - discount) >= AppCommon.EPSILON) {

                                boolean hasrefund = refundService.checkOrderRefundExist(order.getId(), Refund.REFUND_TYPE_PLATFORM_GROUP_DISCOUNT);

                                if (!hasrefund) {
                                    // 有支付行为，发起退款操作，记录退款记录
                                    Refund refund = new Refund();

                                    if(order.getIsselflift() == 1)
                                    {
                                        refund.setMoney(order.getOrdermoney()* refunddiscount);
                                    }
                                    else
                                    {
                                        refund.setMoney(order.getOrderGoods().getGoods_price() * order.getOrderGoods().getBuysum() * refunddiscount);
                                    }

                                    refund.setOrderid(order.getId());
                                    refund.setReason(Refund.REFUND_REASONS[Refund.REFUND_TYPE_PLATFORM_GROUP_DISCOUNT]);
                                    refund.setType(Refund.REFUND_TYPE_PLATFORM_GROUP_DISCOUNT);
                                    refund.setStatus(Refund.REFUND_STATUS_NORMAL);
                                    refund.setUserid(order.getBuyerid());

                                    try {
                                        refund.setCreatetime(new Date());
                                        refundService.createOrUpdate(refund);

                                    } catch (Exception e) {

                                    }

                                    //todo all money refund
                                    WxPayUtils.RefundResult refundResult = WxPayUtils.refund(order,refund);

                                    if (refundResult.getResult() == WxPayUtils.ORDER_RESULT_OK) {
                                        refund.setStatus(Refund.REFUND_STATUS_REFUND_OK);
                                        refund.setRefundid(refundResult.getRefundid());
                                        refund.setRefundtime(new Date());
                                        refundService.createOrUpdate(refund);
                                        //order.setOrderStatus(Order.ORDER_STATUS_REFUND);
                                        order.setRefundFlag(Refund.GetNewRefundFlag(order.getRefundFlag(),Refund.REFUND_TYPE_PLATFORM_GROUP_DISCOUNT,Refund.REFUND_STATUS_REFUND_OK));
                                        logger.warn("deal with super group order  with group id = " + groupid + ",refund ok with order id = " + order.getId());
                                    } else {
                                        logger.warn("deal with super group order  with group id = " + groupid + ",refund fail with order id = " + order.getId() + ",and fail reason = " + refundResult.getErrdes());
                                        refund.setStatus(Refund.REFUND_STATUS_REFUND_FAIL);
                                        refund.setFailstr(refundResult.getErrdes());
                                        refundService.createOrUpdate(refund);
                                        String queuemsg = "" + refund.getId();
                                        delayRefundFailSender.send(queuemsg);

                                        boolean hasKey = redisTemplate.hasKey(RedisKeyConstant.FLAG_REFUND_FAIL);
                                        if (!hasKey) {
                                            //send msg to admin
                                            AliSmsUtils.sendNotify("田趣小集退款异常:", refundResult.getErrdes());
                                            redisTemplate.opsForValue().set(RedisKeyConstant.FLAG_REFUND_FAIL, "1", RedisKeyConstant.EXPIRE_REFUND_FAIL, TimeUnit.HOURS);
                                        }


                                        order.setRefundFlag(Refund.GetNewRefundFlag(order.getRefundFlag(),Refund.REFUND_TYPE_PLATFORM_GROUP_DISCOUNT,Refund.REFUND_STATUS_REFUND_FAIL));


                                    }
                                } else {
                                    logger.warn("deal with group order refund but find refund has beed done with orderid = " + order.getId());
                                }

                            }


                            if (order.getOrdertype() == Order.ORDER_TYPE_GIVER) {
                                order.setOrderStatus(Order.ORDER_STATUS_TOGIFT);
                                // todo send send gift msg
                            } else {
                                if(order.getIsselflift() == 0)
                                {
                                    order.setOrderStatus(Order.ORDER_STATUS_TOSHIP);
                                }
                                else
                                {
                                    String queuemsg = String .valueOf(order.getId()) ;

                                    delayAfterDeliverySender.send(queuemsg);
                                    order.setOrderStatus(Order.ORDER_STATUS_TORECEIVE);
                                }
                            }

                            order.setDiscount(discount);

                            order.setOrder_price(order.getOrder_price() * discount);
                            orderService.UpdateOrder(order);


                            if(order.getOrdertype() != Order.ORDER_TYPE_GIVER)
                            {
                                //customerSettle.onOrderPreSettle(order);
                            }





                        }


                    }
                }



            } else {
                if (teamFounder == null) {
                    logger.warn("deal with group settle but not found msg with id = " + groupid);
                } else {
                    logger.warn("deal with group settle but status is not right with id = " + groupid);
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
