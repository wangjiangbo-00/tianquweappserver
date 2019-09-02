
package com.ziyoushenghuo.weixinoss;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SignatureException;
import java.util.*;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.rabbitmq.schedule.GiftActivityShow;
import com.ziyoushenghuo.response.ResponeCodeConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WxOssUtils {

    private final static COSClient cosClient;

    public static String WXOSS_PREX = "https://tqxjbu-1256942653.cos.ap-chengdu.myqcloud.com/";


    public static class WxOssResult{
        boolean bret;
        String etag;

        public boolean isBret() {
            return bret;
        }

        public void setBret(boolean bret) {
            this.bret = bret;
        }

        public String getEtag() {
            return etag;
        }

        public void setEtag(String etag) {
            this.etag = etag;
        }
    }

    private static Logger logger = LoggerFactory.getLogger(WxOssUtils.class);
    public static  WxOssResult uploadStream(InputStream inputStream,String key,int len) {

        WxOssResult wxOssResult = new WxOssResult();

        String etag = null;
        try {

            // 设置要操作的bucket
            String bucketName = "tqxjbu-1256942653";
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(len);
            objectMetadata.setContentType("image/jpeg");
            //objectMetadata.setSecurityToken("ac87e562c77ae813a6c8bfe8a8b03a2b5263daf030001"); //used for tem key
            PutObjectResult putObjectResult = cosClient.putObject(bucketName, key, inputStream, objectMetadata);

            if(putObjectResult!=null)
            {
                etag = putObjectResult.getETag();
                wxOssResult.setBret(true);
                wxOssResult.setEtag(etag);
            }

        }
        catch (Exception e)
        {

            if (e.getMessage() != null) {
                int mlen = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();

                logger.warn(e.getMessage().substring(0, len));
            } else {

                logger.warn(e.toString());
            }
        }



        return wxOssResult;
    }


    /**
     *
     * @Title: deleteFile
     * @Description: 删除文件
     * @return
     */
    public static void deleteFile(String key) {
        // 设置要操作的bucket


        String bucketName = "tqxjbu-1256942653";

        cosClient.deleteObject(bucketName, key);

        return ;
    }

    static {
        // 微信文档说明cosClient为线程安全的，所以这里公用了
        COSCredentials cred = new BasicCOSCredentials("AKID1xQj1SoSq6Hcm5KLqKjULYNJLI4MMEMk", "PUZHoDVccSRIOLBLNlzFLI83bOXejcO8");
        ClientConfig clientConfig = new ClientConfig(new Region("ap-chengdu"));
        cosClient = new COSClient(cred, clientConfig);
    }



}

