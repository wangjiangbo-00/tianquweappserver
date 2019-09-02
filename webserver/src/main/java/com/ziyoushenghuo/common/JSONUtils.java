package com.ziyoushenghuo.common;



import com.google.gson.Gson;


/* json的简单封装
  @author 王江波
  @version V1.0
*/

public class JSONUtils {

    public static final String COMMON_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 生成md5
     *
     * @param message
     * @return
     */
    public static <T> T parse(String jsonData, Class<T> type){

        Gson gson = new Gson();
        return  gson.fromJson(jsonData,type);

    }

    /**
     * 二进制转十六进制
     *
     * @param Object
     * @return
     */
    public static String toJson(Object ojb) {
        Gson gson = new Gson();
        return  gson.toJson(ojb);
    }


}
