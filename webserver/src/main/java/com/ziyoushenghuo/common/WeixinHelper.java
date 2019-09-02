
package com.ziyoushenghuo.common;


import com.google.gson.Gson;
import com.ziyoushenghuo.entry.Customer;
import com.ziyoushenghuo.entry.CustomerRecommendImg;
import com.ziyoushenghuo.entry.GiftActivity;
import com.ziyoushenghuo.entry.TeamFounder;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

/* 微信二维码生成函数封装
  @author 王江波
  @version V1.0
*/

public class WeixinHelper {


    private static Logger logger = LoggerFactory.getLogger(WeixinHelper.class);

    public static String WEIXIN_QRURL = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=";

    public static int WIDTH = 280;
    public static HttpResponse getuserqr(int userid ,String access_token) throws Exception {
        // 取得图片读入器

        String sceneStr = "rd" + userid;

        HttpResponse response = null;
        try {

            Map<String, Object> param = new HashMap<>();
            param.put("scene", sceneStr);
            param.put("page", "pages/index/index");
            param.put("width", WIDTH);
            param.put("auto_color", false);
            Map<String, Object> line_color = new HashMap<>();
            line_color.put("r", 0);
            line_color.put("g", 0);
            line_color.put("b", 0);
            param.put("line_color", line_color);


            CloseableHttpClient httpClient = HttpClientBuilder.create().build();

            HttpPost httpPost = new HttpPost("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + access_token);
            httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");

            Gson gson = new Gson();

            String body = gson.toJson(param);
            StringEntity entity;
            entity = new StringEntity(body);
            entity.setContentType("image/png");

            httpPost.setEntity(entity);


            response = httpClient.execute(httpPost);
        }catch (Exception e) {
            if (e.getMessage() != null) {
            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();

            logger.warn(e.getMessage().substring(0, len));
        } else {

            logger.warn(e.toString());
        }

        }

        return  response;

    }



    public static HttpResponse getuseractivityqr(Customer user, int type, int refid, String access_token) throws Exception {
        // 取得图片读入器

        String path = "pages/index/index";
        String sceneStr = "";
        switch (type)
        {
            case CustomerRecommendImg.CUSTOM_RECOMMEND_TYPE_LOTTERY:
                path = "pages/lottery/lottery";
                sceneStr = "rd" + user.getId() + ":" + "rf" + refid;
                break;
            case CustomerRecommendImg.CUSTOM_RECOMMEND_TYPE_LOTTERY_RELAY:
                path = "pages/lottery/lottery";

                if(user.getIsrecommender() == 1)
                {
                    sceneStr = "rar" + user.getId() + ":" + "rf" + refid;
                }
                else
                {
                    sceneStr = "ru" + user.getId() + ":" + "rf" + refid;
                }

                break;
            case CustomerRecommendImg.CUSTOM_RECOMMEND_TYPE_SUPERGROUP:
                path = "pages/goodsgroup/goodsgroup";
                sceneStr = "rd" + user.getId() + ":" + "rf" + refid;
                break;
            case CustomerRecommendImg.CUSTOM_RECOMMEND_TYPE_DISCOUNT:
                path = "pages/lottery/lottery";
                break;
            case CustomerRecommendImg.CUSTOM_RECOMMEND_TYPE_GROUP:
                path = "pages/group/group";
                break;

            case CustomerRecommendImg.CUSTOM_RECOMMEND_TYPE_FREEORDER:
                path = "pages/freeorders/freeorders";
                break;

        }

        HttpResponse response = null;
        try {

            Map<String, Object> param = new HashMap<>();
            param.put("scene", sceneStr);
            param.put("page", path);
            param.put("width", WIDTH);
            param.put("auto_color", false);
            Map<String, Object> line_color = new HashMap<>();
            line_color.put("r", 0);
            line_color.put("g", 0);
            line_color.put("b", 0);
            param.put("line_color", line_color);

            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + access_token);
            httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");

            Gson gson = new Gson();
            String body = gson.toJson(param);
            StringEntity entity;
            entity = new StringEntity(body);
            entity.setContentType("image/png");
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);

        }catch (Exception e) {
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();

                logger.warn(e.getMessage().substring(0, len));
            } else {

                logger.warn(e.toString());
            }

        }

        return  response;

    }



    public static HttpResponse getlotteryqr(GiftActivity giftActivity, String access_token) throws Exception {
        // 取得图片读入器

        String path = "pages/lottery/lottery";
        String sceneStr = "rf" + giftActivity.getId();
        HttpResponse response = null;
        try {

            Map<String, Object> param = new HashMap<>();
            param.put("scene", sceneStr);
            param.put("page", path);
            param.put("width", WIDTH);
            param.put("auto_color", false);
            Map<String, Object> line_color = new HashMap<>();
            line_color.put("r", 0);
            line_color.put("g", 0);
            line_color.put("b", 0);
            param.put("line_color", line_color);


            CloseableHttpClient httpClient = HttpClientBuilder.create().build();

            HttpPost httpPost = new HttpPost("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + access_token);
            httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");

            Gson gson = new Gson();

            String body = gson.toJson(param);
            StringEntity entity;
            entity = new StringEntity(body);
            entity.setContentType("image/png");

            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);

        }catch (Exception e) {
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();

                logger.warn(e.getMessage().substring(0, len));
            } else {

                logger.warn(e.toString());
            }

        }

        return  response;

    }


    public static HttpResponse getgroupqr(TeamFounder teamFounder, String access_token) throws Exception {
        // 取得图片读入器

        String path = "pages/goodsgroup/goodsgroup";
        String sceneStr = "rf" + teamFounder.getId();

        HttpResponse response = null;
        try {

            Map<String, Object> param = new HashMap<>();
            param.put("scene", sceneStr);
            param.put("page", path);
            param.put("width", WIDTH);
            param.put("auto_color", false);
            Map<String, Object> line_color = new HashMap<>();
            line_color.put("r", 0);
            line_color.put("g", 0);
            line_color.put("b", 0);
            param.put("line_color", line_color);


            CloseableHttpClient httpClient = HttpClientBuilder.create().build();

            HttpPost httpPost = new HttpPost("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + access_token);
            httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");

            Gson gson = new Gson();

            String body = gson.toJson(param);
            StringEntity entity;
            entity = new StringEntity(body);
            entity.setContentType("image/png");

            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);

        }catch (Exception e) {
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();

                logger.warn(e.getMessage().substring(0, len));
            } else {

                logger.warn(e.toString());
            }

        }

        return  response;

    }


}


