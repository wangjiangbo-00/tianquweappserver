
package com.ziyoushenghuo.weixintemplete;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ziyoushenghuo.asyntask.WeixinTemplateTask;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.common.HttpUtils;
import com.ziyoushenghuo.entry.Customer;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.entry.OrderExpress;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.scheduling.ApiCheckHighFrequency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WeixinTemplate {

    @Autowired
    private WeixinTemplateTask weixinTemplateTask;

    private static Logger logger = LoggerFactory.getLogger(WeixinTemplate.class);



    public  boolean sendTemplateOrderDelery(String token, Order order, Customer customer, OrderExpress orderExpress,boolean asyn){


        //asyn can not get the result
        boolean result = false;
        try {
            Template tem=new Template();
            tem.setTemplateId(TemplateConst.ORDER_DELERY_NOTIFY_ID);
            tem.setTopColor("#00DD00");
            tem.setToUser(customer.getWeixin());
            tem.setPage("pages/order/order?id="+order.getId());

            if(order.getOrdertype() == Order.ORDER_TYPE_NORMAL)
            {
                tem.setPrepay_id(order.getPrepay_id());
                tem.setType(Template.TYPE_ORDERID);
            }
            else
            {
                if(order.getOrderExtra().getFormid() !=null && !order.getOrderExtra().getFormid().isEmpty())
                tem.setPrepay_id(order.getOrderExtra().getFormid());
                tem.setType(Template.TYPE_FORMID);
            }





            List<TemplateParam> paras=new ArrayList<TemplateParam>();
            paras.add(new TemplateParam("keyword1",order.getOrderGoods().getGoodtitle(),"#FF3333"));
            paras.add(new TemplateParam("keyword2",orderExpress.getExpressname(),"#0044BB"));
            paras.add(new TemplateParam("keyword3",orderExpress.getExpressno(),"#0044BB"));
            paras.add(new TemplateParam("keyword4","您的订单已发货，请实时关注物流信息，祝您购物愉快","#0044BB"));


            tem.setTemplateParamList(paras);


            logger.warn("sendTemplateOrderDelery with orderid = " + order.getId() + " to user = " + customer.getWeixin() );

            if(asyn)
            {
                weixinTemplateTask.sendTemplateMsg(token,tem);
            }
            else
            {
                result=TemplateUtils.sendTemplateMsg(token,tem);
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

        return  result;
    }

    public  boolean sendTemplateGiftGivenFree(String token, Order order, Customer customer, String name,boolean asyn){


        //asyn can not get the result
        boolean result = false;
        try {
            Template tem=new Template();
            tem.setTemplateId(TemplateConst.GIFT_GIVEN_NOTIFY_ID);
            tem.setTopColor("#00DD00");
            tem.setToUser(customer.getWeixin());
            tem.setPage("pages/order/order?id="+order.getId());


            tem.setPrepay_id(order.getPrepay_id());
            tem.setType(Template.TYPE_ORDERID);


            String noticemsg = "您的礼品被" + name + "领取时赢得免单奖励,系统正在为您退还费用,快来看看吧";

            List<TemplateParam> paras=new ArrayList<TemplateParam>();
            paras.add(new TemplateParam("keyword1",order.getOrderGoods().getGoodtitle(),"#FF3333"));
            paras.add(new TemplateParam("keyword2",noticemsg,"#0044BB"));

            tem.setTemplateParamList(paras);

            if(asyn)
            {
                weixinTemplateTask.sendTemplateMsg(token,tem);
            }
            else
            {
                result=TemplateUtils.sendTemplateMsg(token,tem);
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

        return  result;
    }

    public  boolean sendTemplateGiftGiven(String token, Order order, Customer customer, String name,boolean asyn){


        //asyn can not get the result
        boolean result = false;
        try {
            Template tem=new Template();
            tem.setTemplateId(TemplateConst.GIFT_GIVEN_NOTIFY_ID);
            tem.setTopColor("#00DD00");
            tem.setToUser(customer.getWeixin());
            tem.setPage("pages/order/order?id="+order.getId());


            tem.setPrepay_id(order.getPrepay_id());
            tem.setType(Template.TYPE_ORDERID);


            String noticemsg = "您的礼品已被" + name + "领取";

            if(order.getPreshippfee()>0 && order.getShipping_money() == 0)
            {
                noticemsg  += ",预付运费正在为您退还";

            }
            else
            {
                noticemsg  += ",快来看看吧";
            }







            List<TemplateParam> paras=new ArrayList<TemplateParam>();
            paras.add(new TemplateParam("keyword1",order.getOrderGoods().getGoodtitle(),"#FF3333"));
            paras.add(new TemplateParam("keyword2",noticemsg,"#0044BB"));



            tem.setTemplateParamList(paras);

            if(asyn)
            {
                weixinTemplateTask.sendTemplateMsg(token,tem);
            }
            else
            {
                result=TemplateUtils.sendTemplateMsg(token,tem);
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

        return  result;
    }


    public  boolean sendLotteryResultNotify(String token,String name, Customer customer,int lotteryid,String formid,boolean asyn){


        //asyn can not get the result
        boolean result = false;
        try {
            Template tem=new Template();
            tem.setTemplateId(TemplateConst.LOTTERY_RESULT_NOTIFY_ID);
            tem.setTopColor("#00DD00");
            tem.setToUser(customer.getWeixin());
            tem.setPage("pages/lottery/lottery?lotteryid="+lotteryid);
            tem.setForm_id(formid);
            tem.setType(Template.TYPE_FORMID);



            List<TemplateParam> paras=new ArrayList<TemplateParam>();
            paras.add(new TemplateParam("keyword1",name,"#FF3333"));
            paras.add(new TemplateParam("keyword2","恭喜你中奖了，快来领取奖品吧","#0044BB"));


            tem.setTemplateParamList(paras);

            if(asyn)
            {
                weixinTemplateTask.sendTemplateMsg(token,tem);
            }
            else
            {
                result=TemplateUtils.sendTemplateMsg(token,tem);
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

        return  result;
    }


    public  boolean sendLotteryAppointment(String token,String name, Customer customer,int lotteryid,String formid,boolean asyn){


        //asyn can not get the result
        boolean result = false;
        try {
            Template tem=new Template();
            tem.setTemplateId(TemplateConst.APPOINTMENT_START_NOTIFY_ID);
            tem.setTopColor("#00DD00");
            tem.setToUser(customer.getWeixin());
            tem.setPage("pages/lottery/lottery?lotteryid="+lotteryid);
            tem.setForm_id(formid);
            tem.setType(Template.TYPE_FORMID);



            List<TemplateParam> paras=new ArrayList<TemplateParam>();
            paras.add(new TemplateParam("keyword1",name,"#FF3333"));
            paras.add(new TemplateParam("keyword2","您参与的抽奖已经开始，快来参与试试手气吧","#0044BB"));


            tem.setTemplateParamList(paras);

            if(asyn)
            {
                weixinTemplateTask.sendTemplateMsg(token,tem);
            }
            else
            {
                result=TemplateUtils.sendTemplateMsg(token,tem);
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

        return  result;
    }


    public  boolean sendGroupAppointment(String token,String name, Customer customer,int groupid,int goodsid,String formid,boolean asyn){


        //asyn can not get the result
        boolean result = false;
        try {
            Template tem=new Template();
            tem.setTemplateId(TemplateConst.APPOINTMENT_START_NOTIFY_ID);
            tem.setTopColor("#00DD00");
            tem.setToUser(customer.getWeixin());
            tem.setPage("pages/goodsgroup/goodsgroup?id="+groupid + "&&goods_id=" + goodsid);
            tem.setForm_id(formid);
            tem.setType(Template.TYPE_FORMID);



            List<TemplateParam> paras=new ArrayList<TemplateParam>();
            paras.add(new TemplateParam("keyword1",name,"#FF3333"));
            paras.add(new TemplateParam("keyword2","您参与的超值团购已经开始，快来参与享受更低折扣吧","#0044BB"));


            tem.setTemplateParamList(paras);

            if(asyn)
            {
                weixinTemplateTask.sendTemplateMsg(token,tem);
            }
            else
            {
                result=TemplateUtils.sendTemplateMsg(token,tem);
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

        return  result;
    }

}
