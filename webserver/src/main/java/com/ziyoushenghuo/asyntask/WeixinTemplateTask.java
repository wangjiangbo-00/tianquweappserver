package com.ziyoushenghuo.asyntask;


import com.ziyoushenghuo.weixintemplete.Template;
import com.ziyoushenghuo.weixintemplete.TemplateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


/* 这里对微信发送模板消息进行了统一的异步封装
  @author 王江波
  @version V1.0
*/
@Component
public class WeixinTemplateTask {

    private static Logger logger = LoggerFactory.getLogger(WeixinTemplateTask.class);

    @Async
    public void sendTemplateMsg(String token,Template template)throws InterruptedException{

        TemplateUtils.sendTemplateMsg(token,template);

    }

}
