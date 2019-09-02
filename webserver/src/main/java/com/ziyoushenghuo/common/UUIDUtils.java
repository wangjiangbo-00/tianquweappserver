package com.ziyoushenghuo.common;

import java.util.UUID;


/* 无用代码，之前想用来生成uuid
  @author 王江波
  @version V1.0
*/
@Deprecated
public class UUIDUtils {

    public static String getUUID() {

        return UUID.randomUUID().toString().replace("-", "");
    }


}
