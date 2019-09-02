package com.ziyoushenghuo.asyntask;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.util.Auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;


/* 七牛异步上传删除，因https暂时启用，保留代码
  @author 王江波
  @version V1.0
*/
@Component
@Deprecated
public class QiniuTask {

    private static Logger logger = LoggerFactory.getLogger(QiniuTask.class);

    @Async
    public void deletepics(List<String> keys) throws InterruptedException{
        Configuration cfg = new Configuration(Zone.zone0());

        String accessKey = "goJrpGbTHF2ZRHc3yVJ8kIPqqSHx7M_UzMiIphtR";
        String secretKey = "22HLpgHF1gKCBrhHmpelsRuMSbd6BYnwxEgq1yOS";
        String bucket = "ziyouclient";


        Auth auth = Auth.create(accessKey, secretKey);


        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            for(String pic:keys)
            {

                logger.info("try to delte qiniu pic : " + pic);
                bucketManager.delete(bucket, pic);
            }

        } catch (QiniuException ex) {
            System.err.println(ex.response.toString());
        }
    }

}
