package com.ziyoushenghuo.asyntask;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.common.WeixinHelper;
import com.ziyoushenghuo.entry.GiftActivity;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.repository.GiftActivityRepository;
import com.ziyoushenghuo.weixinoss.WxOssUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.util.Date;


/* 生成抽奖的二维码(小程序码)
   用于非推广员非主力抽奖使用
  @author 王江波
  @version V1.0
*/

@Component
public class LotteryQrTask {

    private static Logger logger = LoggerFactory.getLogger(LotteryQrTask.class);
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    GiftActivityRepository giftActivityRepository;
    @Async
    public void generateQr(GiftActivity giftActivity) throws InterruptedException{

        try {
            InputStream inputStream = null;
            String access_token = (String) redisUtils.get(RedisKeyConstant.TOKEN_ZXTC_WEIXIN_VALUE);

            HttpResponse response = WeixinHelper.getlotteryqr(giftActivity, access_token);

            long datalen = response.getEntity().getContentLength();
            if (datalen < 256) {
                // deal error
                JsonParser parser = new JsonParser();  //创建JSON解析器

                String responsestr = EntityUtils.toString(response.getEntity());
                JsonObject object = (JsonObject) parser.parse(responsestr);


                if (object != null) {
                    String errcode = object.get("errcode").getAsString();
                    String errmsg = object.get("errmsg").getAsString();

                    logger.warn("generate qr code return err with errcode = " + errcode + " msg = " + errmsg);


                }

            } else {
                inputStream = response.getEntity().getContent();
                String key = "tqxj_img_lottery/" + "id_"+ giftActivity.getId() +"_" + new Date().getTime() ;

                WxOssUtils.WxOssResult wxOssResult = WxOssUtils.uploadStream(inputStream,key,(int)datalen);

                if (wxOssResult.isBret()) {
                    String fullqr = WxOssUtils.WXOSS_PREX + key;

                    giftActivity.setQrpic(fullqr);

                    giftActivityRepository.save(giftActivity);


                }
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

}
