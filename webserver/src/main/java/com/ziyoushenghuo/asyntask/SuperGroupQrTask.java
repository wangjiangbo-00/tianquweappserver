package com.ziyoushenghuo.asyntask;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.qiniu.common.QiniuException;
import com.ziyoushenghuo.common.WeixinHelper;
import com.ziyoushenghuo.entry.TeamFounder;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.repository.TeamFounderRepository;
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


/* 生成超级团购的二维码(小程序码)
   用于非推广员非主力抽奖使用
  @author 王江波
  @version V1.0
*/

@Component
public class SuperGroupQrTask {

    private static Logger logger = LoggerFactory.getLogger(SuperGroupQrTask.class);
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    TeamFounderRepository teamFounderRepository;
    @Async
    public void generateQr(TeamFounder teamFounder) throws InterruptedException{

        try {
            InputStream inputStream = null;
            String access_token = (String) redisUtils.get(RedisKeyConstant.TOKEN_ZXTC_WEIXIN_VALUE);

            HttpResponse response = WeixinHelper.getgroupqr(teamFounder, access_token);

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
                String key = "tqxj_img_supergroup/" + "id_"+ teamFounder.getId() +"_" + new Date().getTime() ;

                WxOssUtils.WxOssResult wxOssResult = WxOssUtils.uploadStream(inputStream,key,(int)datalen);

                if (wxOssResult.isBret()) {
                    String fullqr = WxOssUtils.WXOSS_PREX + key;

                    teamFounder.setQrpic(fullqr);

                    teamFounderRepository.save(teamFounder);


                }
            }

        } catch (QiniuException ex) {
            System.err.println(ex.response.toString());
        }
        catch (Exception e)
        {

        }
    }

}
