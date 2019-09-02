package com.ziyoushenghuo.scheduling;




public final class SchedulingConstant {

    private SchedulingConstant(){
    }
    public static final int COMMON_DELAY = 1000*10;

    public static final int API_CHECK_HIGH_FREQUENCY = 1000*3;
    public static final int API_CHECK_MIDDLE_FREQUENCY = 1000*5;
    public static final int API_CHECK_LOW_FREQUENCY = 1000*10;
    public static final int API_CHECK_QUIET_FREQUENCY = 1000*300;


    public static final int FLAG_CHECK_ORDER_EXPRESS = 1000;


    public static final int FLAG_COMMON_10_MIN = 10;

    public static final int FLAG_COMMON_60_MIN = 60;

    public static final String API_GET_RECOMMEND_SHOPS = "api.getrecommendshops";


}
