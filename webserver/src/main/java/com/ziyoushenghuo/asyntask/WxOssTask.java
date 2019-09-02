package com.ziyoushenghuo.asyntask;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.util.Auth;

import com.ziyoushenghuo.weixinoss.WxOssUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;


/*
  @author 王江波
  @version V1.0
*/
@Component

public class WxOssTask {

    private static Logger logger = LoggerFactory.getLogger(WxOssTask.class);

    @Async
    public void deletepics(List<String> keys) throws InterruptedException{







        try {
            for(String pic:keys)
            {

                logger.info("try to delte qiniu pic : " + pic);
                WxOssUtils.deleteFile(pic);
            }

        } catch (Exception ex) {

        }
    }

}
