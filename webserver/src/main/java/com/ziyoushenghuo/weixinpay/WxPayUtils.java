
package com.ziyoushenghuo.weixinpay;

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


import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.entry.CustomerWithdrew;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.entry.Refund;
import com.ziyoushenghuo.entry.Withdrew;
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

import java.net.InetAddress;


import java.util.Properties;

public class WxPayUtils {

    private static Logger logger = LoggerFactory.getLogger(WxPayUtils.class);

    private static final String ALGORITHM = "AES";


    public static final int ORDER_RESULT_NOMAL = 0;
    public static final int ORDER_RESULT_OK = 1;
    public static final int ORDER_RESULT_FAIL = 2;
    public static final int ORDER_RESULT_EXCEPTION = 3;
    /**
     * 加解密算法/工作模式/填充方式
     */
    private static final String ALGORITHM_MODE_PADDING = "AES/ECB/PKCS5Padding";
    /**
     * 签名字符串
     * @param text需要签名的字符串
     * @param key 密钥
     * @param input_charset编码格式
     * @return 签名结果
     */
    public static String sign(String text, String key, String input_charset) {
        text = text + "&key=" + key;
        return DigestUtils.md5Hex(getContentBytes(text, input_charset));
    }

    public static String getRandomStringByLength(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    /**
     * IpUtils工具类方法
     * 获取真实的ip地址
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if(index != -1){
                return ip.substring(0,index);
            }else{
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
            return ip;
        }
        return request.getRemoteAddr();
    }
    /**
     * 签名字符串
     *  @param text需要签名的字符串
     * @param sign 签名结果
     * @param key密钥
     * @param input_charset 编码格式
     * @return 签名结果
     */
    public static boolean verify(String text, String sign, String key, String input_charset) {
        text = text + "&key=" + key;
        String mysign = DigestUtils.md5Hex(getContentBytes(text, input_charset));
        if (mysign.toUpperCase().equals(sign.toUpperCase())) {
            return true;
        } else {
            return false;
        }
    }

    public static int realMoney(int money_original)
    {
        int rate_money = money_original  * WxPayConfig.pay_rate;

        int check_num = (rate_money/100)%10;

        if(check_num>=5)
        {
            rate_money += 1000;
        }

        rate_money = rate_money/1000;
        return money_original - rate_money;
    }
    /**
     * @param content
     * @param charset
     * @return
     * @throws SignatureException
     * @throws UnsupportedEncodingException
     */
    public static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }

    private static boolean isValidChar(char ch) {
        if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'))
            return true;
        if ((ch >= 0x4e00 && ch <= 0x7fff) || (ch >= 0x8000 && ch <= 0x952f))
            return true;// 简体中文汉字编码
        return false;
    }
    /**
     * 除去数组中的空值和签名参数
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, String> paraFilter(Map<String, String> sArray) {
        Map<String, String> result = new HashMap<String, String>();
        if (sArray == null || sArray.size() <= 0) {
            return result;
        }
        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
                    || key.equalsIgnoreCase("sign_type")) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }
    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if(key != "sign")
            {
                if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                    prestr = prestr + key + "=" + value;
                } else {
                    prestr = prestr + key + "=" + value + "&";
                }
            }

        }
        return prestr;
    }
    /**
     *
     * @param requestUrl请求地址
     * @param requestMethod请求方法
     * @param outputStr参数
     */
    public static String httpRequest(String requestUrl,String requestMethod,String outputStr){
        // 创建SSLContext
        StringBuffer buffer = null;
        try{
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(requestMethod);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            //往服务器端写内容
            if(null !=outputStr){
                OutputStream os=conn.getOutputStream();
                os.write(outputStr.getBytes("utf-8"));
                os.close();
            }
            // 读取服务器端返回的内容
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            buffer = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return buffer.toString();
    }
    public static String urlEncodeUTF8(String source){
        String result=source;
        try {
            result=java.net.URLEncoder.encode(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
     * @param strxml
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public static Map doXMLParse(String strxml) throws Exception {
        if(null == strxml || "".equals(strxml)) {
            return null;
        }

        Map m = new HashMap();
        InputStream in = String2Inputstream(strxml);
        SAXBuilder builder = new SAXBuilder();
        builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl",true);
        builder.setFeature("http://xml.org/sax/features/external-general-entities", false);
        builder.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

        Document doc = builder.build(in);
        Element root = doc.getRootElement();
        List list = root.getChildren();
        Iterator it = list.iterator();
        while(it.hasNext()) {
            Element e = (Element) it.next();
            String k = e.getName();
            String v = "";
            List children = e.getChildren();
            if(children.isEmpty()) {
                v = e.getTextNormalize();
            } else {
                v = getChildrenText(children);
            }

            m.put(k, v);
        }

        //关闭流
        in.close();

        return m;
    }
    /**
     * 获取子结点的xml
     * @param children
     * @return String
     */
    public static String getChildrenText(List children) {
        StringBuffer sb = new StringBuffer();
        if(!children.isEmpty()) {
            Iterator it = children.iterator();
            while(it.hasNext()) {
                Element e = (Element) it.next();
                String name = e.getName();
                String value = e.getTextNormalize();
                List list = e.getChildren();
                sb.append("<" + name + ">");
                if(!list.isEmpty()) {
                    sb.append(getChildrenText(list));
                }
                sb.append(value);
                sb.append("</" + name + ">");
            }
        }

        return sb.toString();
    }
    public static InputStream String2Inputstream(String str) {
        return new ByteArrayInputStream(str.getBytes());
    }


    public static Result queryorder(long orderid ){

       Result queryresult = new Result();

        try{
            //生成的随机字符串
            String nonce_str = WxPayUtils.getRandomStringByLength(32);

            String orderStr = Order.ORDER_WEIXIN_PREX + String.valueOf(orderid);

            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", WxPayConfig.appid);
            packageParams.put("mch_id", WxPayConfig.mch_id);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("out_trade_no", orderStr);//商户订单号


            String prestr = WxPayUtils.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串

            //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
            String mysign = WxPayUtils.sign(prestr, WxPayConfig.key, "utf-8").toUpperCase();

            //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
            String xml = "<xml>" + "<appid>" + WxPayConfig.appid + "</appid>"
                    + "<mch_id>" + WxPayConfig.mch_id + "</mch_id>"
                    + "<nonce_str>" + nonce_str + "</nonce_str>"
                    + "<out_trade_no>" + orderStr + "</out_trade_no>"
                    + "<sign>" + mysign + "</sign>"
                    + "</xml>";

            logger.warn("weixinserver/query request data" + xml);

            //调用统一下单接口，并接受返回的结果
            String result = WxPayUtils.httpRequest(WxPayConfig.query_url, "POST", xml);

            logger.warn("weixinserver/query receive message" + result);

            // 将解析结果存储在HashMap中
            Map map = WxPayUtils.doXMLParse(result);

            String return_code = (String) map.get("return_code");//返回状态码 comm
            String result_code = (String) map.get("result_code");//返回状态码 comm
            String trade_state = (String) map.get("trade_state");//返回状态码 comm
            String err_code  = (String) map.get("err_code ");//返回状态码 comm
            String err_code_des = (String) map.get("err_code_des");//返回状态码 comm
            Map<String, Object> response = new HashMap<String, Object>();//返回给小程序端需要的参数
            if(return_code.equals("SUCCESS") && result_code.equals("SUCCESS") )
            {
                queryresult.setState(trade_state);
                if(trade_state.equals("SUCCESS"))
                {
                    String transaction_id = (String) map.get("transaction_id");
                    queryresult.setResult(ORDER_RESULT_OK);
                    queryresult.setTransaction_id(transaction_id);
                }
                else
                {
                    queryresult.setResult(ORDER_RESULT_FAIL);
                }


            }
            else
            {
                queryresult.setResult(ORDER_RESULT_FAIL);
                queryresult.setErrcode(err_code);
                queryresult.setErrdes(err_code_des);

            }


        }catch(Exception e){
            e.printStackTrace();
            queryresult.setResult(ORDER_RESULT_EXCEPTION);
            queryresult.setErrdes(e.getMessage());
        }

        return  queryresult;
    }


    public static  boolean queryrefund(int orderid,int type ){

        boolean payresult = false;

        try{
            //生成的随机字符串
            String nonce_str = WxPayUtils.getRandomStringByLength(32);

            String orderStr = "";


            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", WxPayConfig.appid);
            packageParams.put("mch_id", WxPayConfig.mch_id);
            packageParams.put("nonce_str", nonce_str);



            orderStr = "zdw_recharge_" + String.valueOf(orderid);
            packageParams.put("out_trade_no", orderStr);//商户订单号


            String prestr = WxPayUtils.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串

            //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
            String mysign = WxPayUtils.sign(prestr, WxPayConfig.key, "utf-8").toUpperCase();

            //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
            String xml = "";

            xml = "<xml>" + "<appid>" + WxPayConfig.appid + "</appid>"
                    + "<mch_id>" + WxPayConfig.mch_id + "</mch_id>"
                    + "<nonce_str>" + nonce_str + "</nonce_str>"
                    + "<out_trade_no>" + orderStr + "</out_trade_no>"
                    + "<sign>" + mysign + "</sign>"
                    + "</xml>";




            logger.warn("weixinserver/query request data" + xml);


            String result = WxPayUtils.httpRequest(WxPayConfig.refund_query, "POST", xml);

            logger.warn("weixinserver/query receive message" + result);

            // 将解析结果存储在HashMap中
            Map map = WxPayUtils.doXMLParse(result);

            String return_code = (String) map.get("return_code");//返回状态码 comm
            String result_code = (String) map.get("result_code");//返回状态码 comm
            String refund_status = (String) map.get("refund_status_0");//返回状态码 comm
            Map<String, Object> response = new HashMap<String, Object>();//返回给小程序端需要的参数
            if(return_code.equals("SUCCESS") && result_code.equals("SUCCESS") && refund_status.equals("SUCCESS"))
            {
                payresult = true;
            }


        }catch(Exception e){
            e.printStackTrace();

        }

        return  payresult;
    }


    public static  boolean closeorder(long orderid,int type ){

        boolean payresult = false;

        try{
            //生成的随机字符串
            String nonce_str = WxPayUtils.getRandomStringByLength(32);

            String orderStr = "";


            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", WxPayConfig.appid);
            packageParams.put("mch_id", WxPayConfig.mch_id);
            packageParams.put("nonce_str", nonce_str);

            orderStr = "zdw_recharge_" + String.valueOf(orderid);
            packageParams.put("out_trade_no", orderStr);//商户订单号

            String prestr = WxPayUtils.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串

            //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
            String mysign = WxPayUtils.sign(prestr, WxPayConfig.key, "utf-8").toUpperCase();

            //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
            String xml = "";

            xml = "<xml>" + "<appid>" + WxPayConfig.appid + "</appid>"
                    + "<mch_id>" + WxPayConfig.mch_id + "</mch_id>"
                    + "<nonce_str>" + nonce_str + "</nonce_str>"
                    + "<out_trade_no>" + orderStr + "</out_trade_no>"
                    + "<sign>" + mysign + "</sign>"
                    + "</xml>";


            logger.warn("weixinserver/query request data" + xml);

            String result = WxPayUtils.httpRequest(WxPayConfig.closeorder_url, "POST", xml);

            logger.warn("weixinserver/query receive message" + result);

            // 将解析结果存储在HashMap中
            Map map = WxPayUtils.doXMLParse(result);

            String return_code = (String) map.get("return_code");//返回状态码 comm
            String result_code = (String) map.get("result_code");//返回状态码 comm

            Map<String, Object> response = new HashMap<String, Object>();//返回给小程序端需要的参数
            if(return_code.equals("SUCCESS") && result_code.equals("SUCCESS") )
            {
                payresult = true;
            }


        }catch(Exception e){
            e.printStackTrace();

        }

        return  payresult;
    }
    public static class RefundResult extends Result
    {
        String refundid;

        public String getRefundid() {
            return refundid;
        }

        public void setRefundid(String refundid) {
            this.refundid = refundid;
        }
    }

    public static class Result
    {
        int result = 0;
        String errcode = "";
        String errdes = "";
        String state = "";

        String transaction_id = "";

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }

        public String getErrcode() {
            return errcode;
        }

        public void setErrcode(String errcode) {
            this.errcode = errcode;
        }

        public String getErrdes() {
            return errdes;
        }

        public void setErrdes(String errdes) {
            this.errdes = errdes;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getTransaction_id() {
            return transaction_id;
        }

        public void setTransaction_id(String transaction_id) {
            this.transaction_id = transaction_id;
        }
    }

    public static  RefundResult refund(Order order ,Refund refund){


        int totalmoney_i  = (int)(order.getOrdermoney()*100);
        int money_i = (int)(refund.getMoney()*100);

        RefundResult refundResult = new RefundResult();

        try{
            //生成的随机字符串
            String nonce_str = WxPayUtils.getRandomStringByLength(32);

            String orderStr = Order.ORDER_WEIXIN_PREX + String.valueOf(order.getId());

            String refundStr = Order.ORDER_REFUND_PREX + String.valueOf(refund.getId());


            String refundReason = "";

            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", WxPayConfig.appid);
            packageParams.put("mch_id", WxPayConfig.mch_id);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("out_trade_no", orderStr);//商户订单号
            packageParams.put("out_refund_no", refundStr);//商户订单号
            packageParams.put("transaction_id", order.getTransaction_id());//商户订单号
            packageParams.put("total_fee", String.valueOf(totalmoney_i));//测试使用0.01
            packageParams.put("refund_fee", String.valueOf(money_i));//测试使用0.01

            if(refund.getType()<Refund.REFUND_REASONS.length)
            {
                refundReason = Refund.REFUND_REASONS[refund.getType()];
            }

            packageParams.put("refund_desc",  refundReason);//测试使用0.01


            String prestr = WxPayUtils.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串

            //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
            String mysign = WxPayUtils.sign(prestr, WxPayConfig.key, "utf-8").toUpperCase();

            //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
            String xml = "<xml>" + "<appid>" + WxPayConfig.appid + "</appid>"
                    + "<mch_id>" + WxPayConfig.mch_id + "</mch_id>"
                    + "<nonce_str>" + nonce_str + "</nonce_str>"
                    + "<out_trade_no>" + orderStr + "</out_trade_no>"
                    + "<out_trade_no>" + orderStr + "</out_trade_no>"
                    + "<out_refund_no>" + refundStr + "</out_refund_no>"
                    + "<transaction_id>" + order.getTransaction_id() + "</transaction_id>"
                    + "<total_fee>" + String.valueOf(totalmoney_i) + "</total_fee>"
                    + "<refund_fee>" + String.valueOf(money_i) + "</refund_fee>"
                    + "<refund_desc>" + refundReason + "</refund_desc>"
                    + "<sign>" + mysign + "</sign>"
                    + "</xml>";

            logger.warn("weixinserver/refund request data" + xml);

            //调用统一下单接口，并接受返回的结果
            String result = ClientCustomSSL.doRefund(WxPayConfig.refund_url , xml);
            logger.warn("weixinserver/refund receive message" + result);
            if(result!=null)
            {
                Map map = WxPayUtils.doXMLParse(result);

                String return_code = (String) map.get("return_code");//返回状态码 comm
                String result_code = (String) map.get("result_code");//返回状态码 comm


                if(return_code.equals("SUCCESS") && result_code.equals("SUCCESS") )
                {
                    refundResult.setResult(ORDER_RESULT_OK);
                    String refund_id = (String) map.get("refund_id");//返回状态码 comm

                    refundResult.setRefundid(refund_id);

                }
                else
                {
                    refundResult.setResult(ORDER_RESULT_FAIL);
                    if(return_code.equals("SUCCESS"))
                    {


                        String err_code = (String) map.get("err_code");//返回状态码 comm
                        String err_des = (String) map.get("err_code_des");//返回状态码 comm
                        if(err_code!=null)
                        {
                            refundResult.setErrcode(err_code);
                            refundResult.setErrdes(err_des);
                        }

                    }
                    else
                    {



                        String return_msg = (String) map.get("return_msg");//返回状态码 comm
                        if(return_msg!=null)
                        {
                            refundResult.setErrcode(Refund.REFUND_DEFAULT_ERROR_CODE);
                            refundResult.setErrdes(return_msg);
                        }
                    }
                }
            }

            else
            {
                refundResult.setResult(ORDER_RESULT_FAIL);
                refundResult.setErrcode("NOTHINGRETRUN");
                refundResult.setErrdes("申请未返回任何内容，查询网络或者证书");
            }



            // 将解析结果存储在HashMap中



        }catch(Exception e){
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();

                logger.warn(e.getMessage().substring(0, len));
            } else {

                logger.warn(e.toString());
            }

        }


        return  refundResult;
    }


    public static  RefundResult withdrew(Withdrew withdrew ){



        RefundResult refundResult = new RefundResult();

        try{
            int totalmoney_i  = (int)(withdrew.getMoney()*100);


            InetAddress addr = InetAddress.getLocalHost();
            String ip=addr.getHostAddress().toString(); //获取本机ip

            String act_name = "商户收益提现";


            //生成的随机字符串
            String nonce_str = WxPayUtils.getRandomStringByLength(32);

            String orderStr = Order.ORDER_SHOP_WITHDREW_PREX + String.valueOf(withdrew.getId());


            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("mch_appid", WxPayConfig.owner_appid);
            packageParams.put("mchid", WxPayConfig.mch_id);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("partner_trade_no", orderStr);//商户订单号
            packageParams.put("openid", withdrew.getOpenid());//商户订单号
            packageParams.put("check_name", "NO_CHECK");//商户订单号
            packageParams.put("amount", String.valueOf(totalmoney_i));//测试使用0.01
            packageParams.put("desc", Order.ORDER_SHOP_WITHDREW_DESC);//测试使用0.01
            packageParams.put("spbill_create_ip", ip);//测试使用0.01





            String prestr = WxPayUtils.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串

            //String mycompanysign = WxPayUtils.sign(prestr, WxPayConfig.companypaykey, "utf-8").toUpperCase();

            //packageParams.put("workwx_sign", mycompanysign);


            //String prestrwithcompanysign = WxPayUtils.createLinkString(packageParams);

            //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
            String mysign = WxPayUtils.sign(prestr, WxPayConfig.key, "utf-8").toUpperCase();



            //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
            String xml = "<xml>" + "<mch_appid>" + WxPayConfig.owner_appid + "</mch_appid>"
                    + "<mchid>" + WxPayConfig.mch_id + "</mchid>"
                    + "<nonce_str>" + nonce_str + "</nonce_str>"
                    + "<sign>" + mysign + "</sign>"
                    + "<partner_trade_no>" + orderStr + "</partner_trade_no>"
                    + "<openid>" + withdrew.getOpenid() + "</openid>"
                    + "<check_name>" + "NO_CHECK" + "</check_name>"
                    + "<amount>" + String.valueOf(totalmoney_i) + "</amount>"
                    + "<desc>" + Order.ORDER_SHOP_WITHDREW_DESC + "</desc>"
                    + "<spbill_create_ip>" + ip + "</spbill_create_ip>"


                    + "</xml>";

            logger.warn("weixinserver/withdrew request data" + xml);

            //调用统一下单接口，并接受返回的结果
            String result = ClientCustomSSL.dowithdrew(WxPayConfig.cpay_url , xml);
            logger.warn("weixinserver/withdrew receive message" + result);
            if(result!=null)
            {
                Map map = WxPayUtils.doXMLParse(result);

                String return_code = (String) map.get("return_code");//返回状态码 comm
                String result_code = (String) map.get("result_code");//返回状态码 comm


                if(return_code.equals("SUCCESS") && result_code.equals("SUCCESS") )
                {
                    refundResult.setResult(ORDER_RESULT_OK);
                    String refund_id = (String) map.get("payment_no");//返回状态码 comm

                    refundResult.setRefundid(refund_id);

                }
                else
                {
                    refundResult.setResult(ORDER_RESULT_FAIL);
                    if(return_code.equals("SUCCESS"))
                    {
                        String err_code = (String) map.get("err_code");//返回状态码 comm
                        String err_des = (String) map.get("err_code_des");//返回状态码 comm
                        if(err_code!=null)
                        {
                            refundResult.setErrcode(err_code);
                            refundResult.setErrdes(err_des);
                        }

                    }
                    else
                    {



                        String return_msg = (String) map.get("return_msg");//返回状态码 comm
                        if(return_msg!=null)
                        {
                            refundResult.setErrcode(Refund.REFUND_DEFAULT_ERROR_CODE);
                            refundResult.setErrdes(return_msg);
                        }
                    }
                }
            }
            else
            {
                refundResult.setResult(ORDER_RESULT_FAIL);
                refundResult.setErrcode("NOTHINGRETRUN");
                refundResult.setErrdes("申请未返回任何内容，查询网络或者证书");
            }



            // 将解析结果存储在HashMap中



        }catch(Exception e){
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();

                logger.warn(e.getMessage().substring(0, len));
            } else {

                logger.warn(e.toString());
            }

        }


        return  refundResult;
    }



    public static  RefundResult customerwithdrew(CustomerWithdrew customerWithdrew ){



        RefundResult refundResult = new RefundResult();

        try{
            int totalmoney_i  = (int)(customerWithdrew.getMoney()*100);


            InetAddress addr = InetAddress.getLocalHost();
            String ip=addr.getHostAddress().toString(); //获取本机ip

            String act_name = "推广提现支出";


            //生成的随机字符串
            String nonce_str = WxPayUtils.getRandomStringByLength(32);

            String orderStr = Order.ORDER_CUSTOMER_WITHDREW_PREX + String.valueOf(customerWithdrew.getId());


            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("mch_appid", WxPayConfig.appid);
            packageParams.put("mchid", WxPayConfig.mch_id);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("partner_trade_no", orderStr);//商户订单号
            packageParams.put("openid", customerWithdrew.getOpenid());//商户订单号
            packageParams.put("check_name", "NO_CHECK");//商户订单号
            packageParams.put("amount", String.valueOf(totalmoney_i));//测试使用0.01
            packageParams.put("desc", Order.ORDER_CUSTOMER_WITHDREW_DESC);//测试使用0.01
            packageParams.put("spbill_create_ip", ip);//测试使用0.01





            String prestr = WxPayUtils.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串

            //String mycompanysign = WxPayUtils.sign(prestr, WxPayConfig.companypaykey, "utf-8").toUpperCase();

            //packageParams.put("workwx_sign", mycompanysign);


            //String prestrwithcompanysign = WxPayUtils.createLinkString(packageParams);

            //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
            String mysign = WxPayUtils.sign(prestr, WxPayConfig.key, "utf-8").toUpperCase();



            //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
            String xml = "<xml>" + "<mch_appid>" + WxPayConfig.appid + "</mch_appid>"
                    + "<mchid>" + WxPayConfig.mch_id + "</mchid>"
                    + "<nonce_str>" + nonce_str + "</nonce_str>"
                    + "<sign>" + mysign + "</sign>"
                    + "<partner_trade_no>" + orderStr + "</partner_trade_no>"
                    + "<openid>" + customerWithdrew.getOpenid() + "</openid>"
                    + "<check_name>" + "NO_CHECK" + "</check_name>"
                    + "<amount>" + String.valueOf(totalmoney_i) + "</amount>"
                    + "<desc>" + Order.ORDER_CUSTOMER_WITHDREW_DESC + "</desc>"
                    + "<spbill_create_ip>" + ip + "</spbill_create_ip>"


                    + "</xml>";

            logger.warn("weixinserver/withdrew request data" + xml);

            //调用统一下单接口，并接受返回的结果
            String result = ClientCustomSSL.dowithdrew(WxPayConfig.cpay_url , xml);
            logger.warn("weixinserver/withdrew receive message" + result);
            if(result!=null)
            {
                Map map = WxPayUtils.doXMLParse(result);

                String return_code = (String) map.get("return_code");//返回状态码 comm
                String result_code = (String) map.get("result_code");//返回状态码 comm


                if(return_code.equals("SUCCESS") && result_code.equals("SUCCESS") )
                {
                    refundResult.setResult(ORDER_RESULT_OK);
                    String refund_id = (String) map.get("payment_no");//返回状态码 comm

                    refundResult.setRefundid(refund_id);

                }
                else
                {
                    refundResult.setResult(ORDER_RESULT_FAIL);
                    if(return_code.equals("SUCCESS"))
                    {
                        String err_code = (String) map.get("err_code");//返回状态码 comm
                        String err_des = (String) map.get("err_code_des");//返回状态码 comm
                        if(err_code!=null)
                        {
                            refundResult.setErrcode(err_code);
                            refundResult.setErrdes(err_des);
                        }

                    }
                    else
                    {



                        String return_msg = (String) map.get("return_msg");//返回状态码 comm
                        if(return_msg!=null)
                        {
                            refundResult.setErrcode(Refund.REFUND_DEFAULT_ERROR_CODE);
                            refundResult.setErrdes(return_msg);
                        }
                    }
                }
            }
            else
            {
                refundResult.setResult(ORDER_RESULT_FAIL);
                refundResult.setErrcode("NOTHINGRETRUN");
                refundResult.setErrdes("申请未返回任何内容，查询网络或者证书");
            }



            // 将解析结果存储在HashMap中



        }catch(Exception e){
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();

                logger.warn(e.getMessage().substring(0, len));
            } else {

                logger.warn(e.toString());
            }

        }


        return  refundResult;
    }

    public static byte[] decode(String  key) throws Exception {
        System.out.println(new BASE64Decoder().decodeBuffer(key));
        return new BASE64Decoder().decodeBuffer(key);

    }

    public static String decryptData(String base64Data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING);
        String keyMd5 = DigestUtils.md5Hex(WxPayUtils.getContentBytes(WxPayConfig.key, "utf-8")).toLowerCase();
        SecretKeySpec key = new SecretKeySpec(keyMd5.getBytes(), ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(decode(base64Data)),"UTF-8");
    }
}
