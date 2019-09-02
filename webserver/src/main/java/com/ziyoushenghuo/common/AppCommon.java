package com.ziyoushenghuo.common;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/* 共用函数库，目前封装了2个随机算法
  @author 王江波
  @version V1.0
*/

public class AppCommon {

    public static final String COMMON_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final Double EPSILON = 0.000001;

    public static final int MIN_FREE_CHANCE = 1;

    public static final int DEFAULT_EXCEPTION_LEN = 150;

    public static final Object ownerProfitLock = new Object();

    public static final String getRandowString(int n)
    {
        if(n>10 )
        {
            n = 10;
        }
        if(n<1)
        {
            n = 6;
        }


        StringBuilder sb = new StringBuilder(n);
        String s = "abcdefghijklmnopqrstuvwxyz0123456789";
        char[] c = s.toCharArray();
        Random random = new Random();
        // 没有参数时，以当前毫秒值作为种子
        for( int i = 0; i < n; i ++) {
            sb.append(c[random.nextInt(c.length)]);
        }

        return sb.toString();
    }

    public static final List<Integer> getRandomSubList(List<Integer>in,int num)
    {

        if(in == null)
        {
            return  null;
        }

        // important this function only used for num is really small like < 5
        int size = in.size();
        if(num < 1 )
        {
            num = 1;
        }

        if(num>in.size())
        {
            num = in.size();

            return in;
        }



        Random random = new Random();
        Set<Integer> integers = new HashSet<>(num);

        List<Integer> results = new ArrayList<>(num);
        // 没有参数时，以当前毫秒值作为种子
        for( int i = 0; i < num; i ++) {
            int pos = random.nextInt(size);
            while (integers.contains(pos))
            {
                pos = random.nextInt(size);
            }
            results.add(in.get(pos));
            integers.add(in.get(pos));
        }

        return  results;
    }


    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

}
