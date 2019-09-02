
package com.ziyoushenghuo.weixintoken;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SignatureException;
import java.util.*;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.entry.Refund;
import com.ziyoushenghuo.weixinpay.WxPayConfig;
import com.ziyoushenghuo.weixinpay.WxPayUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;


import java.util.Properties;

public class WxTokenUtils {

    private static Logger logger = LoggerFactory.getLogger(WxTokenUtils.class);

    public static  WxTokenResult gettoken( ){

        WxTokenResult tokenResult = new WxTokenResult();

        try{
            //生成的随机字符串
            String url = WxPayConfig.token_url;

            url+=("&appid=" + WxPayConfig.appid);
            url+=("&secret=" + WxPayConfig.APP_SECRET);


            //调用统一下单接口，并接受返回的结果
            String result = WxPayUtils.httpRequest(url, "GET", "");
            logger.warn("weixin gettoken" + result);
            if(result!=null){
                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();

                JsonObject jerrcode = jsonObject.getAsJsonObject("errcode");
                if(jerrcode != null)
                {
                    tokenResult.setErrcode(jsonObject.get("errcode").getAsString());
                    tokenResult.setErrmsg(jsonObject.get("errmsg").getAsString());
                }
                else
                {
                    tokenResult.setToken(jsonObject.get("access_token").getAsString());
                    tokenResult.setExpires_in(jsonObject.get("expires_in").getAsInt());
                }
            }






        }catch(Exception e){
            e.printStackTrace();

        }

        return  tokenResult;
    }



}
