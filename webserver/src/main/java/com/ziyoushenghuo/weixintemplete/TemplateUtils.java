
package com.ziyoushenghuo.weixintemplete;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ziyoushenghuo.common.HttpUtils;
import com.ziyoushenghuo.entry.Customer;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.scheduling.ApiCheckHighFrequency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;

public class TemplateUtils {

    private static Logger logger = LoggerFactory.getLogger(TemplateUtils.class);

    public  static  boolean sendTemplateMsg(String token,Template template){

        boolean flag=false;

        String requestUrl="https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=ACCESS_TOKEN";
        requestUrl=requestUrl.replace("ACCESS_TOKEN", token);

        String json = template.toJSON();
        String result = HttpUtils.httpRequest(requestUrl, "POST", json);
        JsonObject jsonResult = new JsonParser().parse(result).getAsJsonObject();
        if(jsonResult!=null){
            int errorCode=jsonResult.get("errcode").getAsInt();
            String errorMessage=jsonResult.get("errmsg").getAsString();
            if(errorCode==0){
                flag=true;
            }else{
                logger.warn("模板消息发送失败:"+errorCode+","+errorMessage);
                flag=false;
            }
        }
        return flag;

    }






}
