
package com.ziyoushenghuo.common;


import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/* 七牛的工具函数，保存代码
  @author 王江波
  @version V1.0
*/
@Deprecated
public class QiniuHelper {

    public static String QINIU_PREX = "http://www.imgtqbu.weiruikj.cn/";


    public static String GetKey(String url)
    {
        return url.substring(QINIU_PREX.length());
    }


    private static Logger logger = LoggerFactory.getLogger(QiniuHelper.class);
    public static String upload(byte[] src)  {
        // 取得图片读入器
        Configuration cfg = new Configuration(Zone.zone0());

        UploadManager uploadManager = new UploadManager(cfg);

        String accessKey = "goJrpGbTHF2ZRHc3yVJ8kIPqqSHx7M_UzMiIphtR";
        String secretKey = "22HLpgHF1gKCBrhHmpelsRuMSbd6BYnwxEgq1yOS";
        String bucket = "ziyoutechan";

        String key = null;
        String hash = "";
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response qnresponse = uploadManager.put(src, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(qnresponse.bodyString(), DefaultPutRet.class);

            if(putRet!=null)
            {
                hash = putRet.key;


            }

        } catch (Exception e) {

            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();

                logger.warn(e.getMessage().substring(0, len));
            } else {

                logger.warn(e.toString());
            }
        }

        return hash;
    }


    public static Boolean delete(String key) {
        // 取得图片读入器

        Boolean bRet = false;
        Configuration cfg = new Configuration(Zone.zone0());

        String accessKey = "goJrpGbTHF2ZRHc3yVJ8kIPqqSHx7M_UzMiIphtR";
        String secretKey = "22HLpgHF1gKCBrhHmpelsRuMSbd6BYnwxEgq1yOS";
        String bucket = "ziyoutechan";


        Auth auth = Auth.create(accessKey, secretKey);


        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            Response qnresponse =bucketManager.delete(bucket, key);

            if(qnresponse!=null)
            {
                bRet = true;
            }

        } catch (Exception e) {
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();

                logger.warn(e.getMessage().substring(0, len));
            } else {

                logger.warn(e.toString());
            }
        }

        return  bRet;
    }



}


