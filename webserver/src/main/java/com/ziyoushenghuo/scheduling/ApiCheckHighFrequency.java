package com.ziyoushenghuo.scheduling;



import com.qcloud.Module.Sts;
import com.qcloud.QcloudApiModuleCenter;
import com.qcloud.Utilities.Json.JSONObject;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.entry.OrderExtra;
import com.ziyoushenghuo.entry.Refund;
import com.ziyoushenghuo.rabbitmq.delay.DelayCancalRefundSender;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.repository.OrderExtraRepository;
import com.ziyoushenghuo.service.OrderService;
import com.ziyoushenghuo.web.controller.SystemConfigController;
import com.ziyoushenghuo.weixinoss.CosStsClient;
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
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

/* 这里最初与php后台操作都是通过redis周期性读取，后来的通过接口，有机会把这里面的判断全部转移到接口调用
  @author 王江波
  @version V1.0
*/
@Component
public class ApiCheckHighFrequency {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private OrderService orderService;

    @Autowired
    private DelayCancalRefundSender delayCancalRefundSender;


    @Autowired
    private OrderExtraRepository orderExtraRepository;

    private static Logger logger = LoggerFactory.getLogger(ApiCheckHighFrequency.class);

    @Scheduled(initialDelay = SchedulingConstant.COMMON_DELAY,fixedRate = SchedulingConstant.API_CHECK_HIGH_FREQUENCY)
    public void task()
    {
        checkGoodsCategorys();
        checkGoodsDetails();
        checkRecommendGoods();
        checkRecommendShops();
        checkWeixinToken();
        checkTxOssToken();
        checkQiniuToken();
        checkShopGoods();
        checkShopFeeList();
        checkOrderSetRefund();
    }

    private void  checkRecommendShops()
    {
        try {
            String api_get_recommend_shops = redisTemplate.opsForValue().get(RedisKeyConstant.API_GET_RECOMMEND_SHOPS);
            if(api_get_recommend_shops!=null)
            {
                redisTemplate.delete(RedisKeyConstant.VALUE_GET_RECOMMEND_SHOPS);
                redisUtils.remove(RedisKeyConstant.API_GET_RECOMMEND_SHOPS);
            }
        }catch (Exception e)
        {
            int len = e.getMessage().length()> AppCommon.DEFAULT_EXCEPTION_LEN?AppCommon.DEFAULT_EXCEPTION_LEN:e.getMessage().length();
            logger.warn(e.getMessage().substring(0,len));
        }

    }


    private void  checkShopGoods()
    {

        try {
            List<Object> api_get_recommend_goods = redisUtils.lRange(RedisKeyConstant.API_GET_SHOP_GOODS,0,-1);

            if(api_get_recommend_goods!=null && !api_get_recommend_goods.isEmpty())
            {
                redisUtils.hmDelete(RedisKeyConstant.VALUE_GET_SHOP_GOODS,api_get_recommend_goods.toArray());
                redisUtils.remove(RedisKeyConstant.API_GET_SHOP_GOODS);
            }
        }catch (Exception e)
        {
            int len = e.getMessage().length()> AppCommon.DEFAULT_EXCEPTION_LEN?AppCommon.DEFAULT_EXCEPTION_LEN:e.getMessage().length();
            logger.warn(e.getMessage().substring(0,len));
        }

    }


    private void  checkOrderSetRefund()
    {
        //todo
        try {
            List<Object> orderstrs = redisUtils.lRange(RedisKeyConstant.FLAG_ORDER_SETREFUND,0,-1);
            redisTemplate.delete(RedisKeyConstant.FLAG_ORDER_SETREFUND);
            if(!orderstrs.isEmpty())
            {
                for(Object orderstr :orderstrs)
                {
                    int orderid = Integer.valueOf(orderstr.toString());

                    Order order = orderService.getOrderById(orderid);

                    if(order!=null)
                    {
                        logger.warn("deal with order user cancal order with orderid = " + order.getId());
                        if(order.getOrderStatus() == Order.ORDER_STATUS_TOSHIP || order.getOrderStatus() == Order.ORDER_STATUS_TOGIFT )
                        {

                            order.setTryrefund(2);
                            orderService.UpdateOrder(order);

                            String queuemsg = String .valueOf(order.getId()) ;

                            delayCancalRefundSender.send(queuemsg);
                        }
                        else
                        {
                            logger.warn("deal with order user cancal order with orderid =  " + order.getId() + "status not right = " + order.getOrderStatus());
                        }
                    }

                }
            }
        }catch (Exception e)
        {
            int len = e.getMessage().length()> AppCommon.DEFAULT_EXCEPTION_LEN?AppCommon.DEFAULT_EXCEPTION_LEN:e.getMessage().length();
            logger.warn(e.getMessage().substring(0,len));
        }

    }

    private void  checkGoodsCategorys()
    {

        try {
            String api_get_GoodsCategorys = redisTemplate.opsForValue().get(RedisKeyConstant.API_GET_CATEGORYS);
            if(api_get_GoodsCategorys!=null)
            {
                redisUtils.remove(RedisKeyConstant.VALUE_GET_CATEGORYS);
                redisUtils.remove(RedisKeyConstant.API_GET_CATEGORYS);
            }
        }catch (Exception e)
        {
            int len = e.getMessage().length()> AppCommon.DEFAULT_EXCEPTION_LEN?AppCommon.DEFAULT_EXCEPTION_LEN:e.getMessage().length();
            logger.warn(e.getMessage().substring(0,len));
        }

    }

    private void  checkShopFeeList()
    {

        try {
            List<Object> api_get_recommend_goods = redisUtils.lRange(RedisKeyConstant.API_GET_SHOPFEELIST,0,-1);
            if(api_get_recommend_goods!=null && !api_get_recommend_goods.isEmpty())
            {
                redisUtils.hmDelete(RedisKeyConstant.VALUE_GET_SHOP_FEELIST,api_get_recommend_goods.toArray());
                redisUtils.remove(RedisKeyConstant.API_GET_SHOPFEELIST);
            }
        }catch (Exception e)
        {
            int len = e.getMessage().length()> AppCommon.DEFAULT_EXCEPTION_LEN?AppCommon.DEFAULT_EXCEPTION_LEN:e.getMessage().length();
            logger.warn(e.getMessage().substring(0,len));
        }

    }

    private void  checkRecommendGoods()
    {
        try {
            List<Object> api_get_recommend_goods = redisUtils.lRange(RedisKeyConstant.API_GET_RECOMMEND_GOODS,0,-1);

            if(api_get_recommend_goods!=null && !api_get_recommend_goods.isEmpty())
            {
                redisUtils.hmDelete(RedisKeyConstant.VALUE_GET_RECOMMEND_GOODS,api_get_recommend_goods.toArray());
                redisUtils.remove(RedisKeyConstant.API_GET_RECOMMEND_GOODS);
            }
        }catch (Exception e)
        {
            int len = e.getMessage().length()> AppCommon.DEFAULT_EXCEPTION_LEN?AppCommon.DEFAULT_EXCEPTION_LEN:e.getMessage().length();
            logger.warn(e.getMessage().substring(0,len));
        }

    }
    private void  checkGoodsDetails()
    {
        try {
            List<Object> api_get_goodsdetails = redisUtils.lRange(RedisKeyConstant.API_GET_GOODS_DETAILS,0,-1);

            if(api_get_goodsdetails!=null && !api_get_goodsdetails.isEmpty())
            {
                redisUtils.hmDelete(RedisKeyConstant.VALUE_GET_GOODS_DETAILS_V1,api_get_goodsdetails.toArray());
                redisUtils.remove(RedisKeyConstant.API_GET_GOODS_DETAILS);
            }
        }catch (Exception e)
        {
            int len = e.getMessage().length()> AppCommon.DEFAULT_EXCEPTION_LEN?AppCommon.DEFAULT_EXCEPTION_LEN:e.getMessage().length();
            logger.warn(e.getMessage().substring(0,len));
        }

    }


    private void  checkWeixinToken()
    {

        try {
            String weixintokenflag = redisTemplate.opsForValue().get(RedisKeyConstant.TOKEN_ZXTC_WEIXIN_FLAG);
            if(weixintokenflag == null)
            {

                WxTokenResult wxTokenResult = WxTokenUtils.gettoken();


                if(wxTokenResult.getToken()!=null)
                {
                    redisUtils.set(RedisKeyConstant.TOKEN_ZXTC_WEIXIN_VALUE,wxTokenResult.getToken());
                    redisUtils.set(RedisKeyConstant.TOKEN_ZXTC_WEIXIN_FLAG,"1",(long)((wxTokenResult.getExpires_in()*8)/10), TimeUnit.SECONDS);
                }

                //get token and expire

            }
        }catch (Exception e)
        {
            int len = e.getMessage().length()> AppCommon.DEFAULT_EXCEPTION_LEN?AppCommon.DEFAULT_EXCEPTION_LEN:e.getMessage().length();
            logger.warn(e.getMessage().substring(0,len));
        }

    }


    private void  checkQiniuToken()
    {


        try {
            String weixintokenflag = redisTemplate.opsForValue().get(RedisKeyConstant.TOKEN_ZXTC_QINIU_FLAG);
            if(weixintokenflag == null)
            {

                String accessKey = "goJrpGbTHF2ZRHc3yVJ8kIPqqSHx7M_UzMiIphtR";
                String secretKey = "22HLpgHF1gKCBrhHmpelsRuMSbd6BYnwxEgq1yOS";
                String bucket = "ziyouclient";


                Auth auth = Auth.create(accessKey, secretKey);
                String upToken = auth.uploadToken(bucket);


                if(upToken!=null)
                {
                    redisUtils.set(RedisKeyConstant.TOKEN_ZXTC_QINIU_VALUE,upToken);
                    redisUtils.set(RedisKeyConstant.TOKEN_ZXTC_QINIU_FLAG,"1",(long)((3600*8)/10), TimeUnit.SECONDS);
                }

                //get token and expire

            }
        }catch (Exception e)
        {
            int len = e.getMessage().length()> AppCommon.DEFAULT_EXCEPTION_LEN?AppCommon.DEFAULT_EXCEPTION_LEN:e.getMessage().length();
            logger.warn(e.getMessage().substring(0,len));
        }


    }


    private void  checkTxOssToken()
    {

        try {
            String weixintokenflag = redisTemplate.opsForValue().get(RedisKeyConstant.TOKEN_ZXTC_TXOSS_FLAG);
            if(weixintokenflag == null)
            {

                try {

                    TreeMap<String, Object> config = new TreeMap<String, Object>();
                    config.put("SecretId", "AKID1xQj1SoSq6Hcm5KLqKjULYNJLI4MMEMk");
                    config.put("SecretKey", "PUZHoDVccSRIOLBLNlzFLI83bOXejcO8");
        /* 请求方法类型 POST、GET */
                    config.put("RequestMethod", "GET");

        /* 区域参数，可选: gz: 广州; sh: 上海; hk: 香港; ca: 北美; 等。 */
                    config.put("Region", "ap-chengdu");

                    QcloudApiModuleCenter module = new QcloudApiModuleCenter(new Sts(),
                            config);

                    TreeMap<String, Object> params = new TreeMap<String, Object>();
        /* 将需要输入的参数都放入 params 里面，必选参数是必填的。 */
        /* DescribeInstances 接口的部分可选参数如下 */
                    params.put("name", "396511701@qq.com");
                    String policy = "{\"statement\": [{\"action\": [\"name/cos:*\"],\"effect\": \"allow\",\"resource\":\"*\"}],\"version\": \"2.0\"}";
                    params.put("policy", policy);

                    params.put("durationSeconds", 7200);

                    module.generateUrl("GetFederationToken", params);
                    String result = null;
                    try {
            /* call 方法正式向指定的接口名发送请求，并把请求参数 params 传入，返回即是接口的请求结果。 */
                        result = module.call("GetFederationToken", params);
                        JSONObject json_result = new JSONObject(result);

                        if(json_result!=null)
                        {
                            int code = json_result.getInt("code");
                            if(code == 0)
                            {
                                redisUtils.set(RedisKeyConstant.TOKEN_ZXTC_TXOSS_VALUE,result);
                                redisUtils.set(RedisKeyConstant.TOKEN_ZXTC_TXOSS_FLAG,"1",(long)((3600*8)/10), TimeUnit.SECONDS);
                            }
                            else
                            {
                                logger.warn("get weixin oss templeid with ret");
                            }
                        }
                        System.out.println(json_result);
                    } catch (Exception e) {
                        System.out.println("error..." + e.getMessage());
                    }






                } catch (Exception e) {
                    throw new IllegalArgumentException("no valid secret !");
                }





        /* 在这里指定所要用的签名算法，不指定默认为 HmacSHA1*/
                //params.put("SignatureMethod", "HmacSHA256");

        /* generateUrl 方法生成请求串, 可用于调试使用 */







                //get token and expire

            }
        }catch (Exception e)
        {
            int len = e.getMessage().length()> AppCommon.DEFAULT_EXCEPTION_LEN?AppCommon.DEFAULT_EXCEPTION_LEN:e.getMessage().length();
            logger.warn(e.getMessage().substring(0,len));
        }

    }



}
