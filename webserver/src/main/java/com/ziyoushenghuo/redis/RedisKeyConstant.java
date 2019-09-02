package com.ziyoushenghuo.redis;




public final class RedisKeyConstant {

    private RedisKeyConstant(){
    }



    public static final String API_GET_RECOMMEND_SHOPS = "apicheck.getrecommendshops";
    public static final String API_GET_RECOMMEND_GOODS = "apicheck.getrecommendgoods";
    public static final String API_GET_SHOP_GOODS = "apicheck.getshopgoods";
    public static final String API_GET_GOODS_DETAILS = "apicheck.getgoodsdetails";
    public static final String API_GET_CATEGORYS = "apicheck.getcategorys";
    public static final String API_GET_SHOPDETAILS = "apicheck.getshopdetails";
    public static final String API_GET_SHOPFEELIST = "apicheck.getshopfeelist";
    public static final String FLAG_PLATFORM_COMMON_10_MIN = "flag.getplatformcommon10min";
    public static final String FLAG_PLATFORM_COMMON_60_MIN = "flag.getplatformcommon60min";
    public static final String FLAG_ORDER_SETREFUND = "flag.order_setrefund";
    public static final String FLAG_ORDER_EXPRESS = "flag.order_express";
    public static final String FLAG_REFUND_FAIL = "flag.refund_fail";



    public static final String VALUE_GET_RECOMMEND_SHOPS = "value.getrecommendshops";
    public static final String VALUE_GET_RECOMMEND_GOODS = "value.getrecommendgoods";
    public static final String VALUE_GET_SHOP_GOODS = "value.getshopgoods";
    public static final String VALUE_GET_CATEGORYS = "value.getcategorys";
    public static final String VALUE_GET_GOODS_DETAILS = "value.getgoodsdetails";
    public static final String VALUE_GET_GOODS_DETAILS_V1 = "value.getgoodsdetails_v1";
    public static final String VALUE_GET_SHOP_FEELIST = "value.getshopfeelist";
    public static final String VALUE_GET_PLATFORM_LOTTERY = "value.getplatformlottery";
    public static final String VALUE_GET_PLATFORM_GROUP = "value.getplatformgroup";
    public static final String VALUE_GET_PLATFORM_DISCOUNT_GOODS = "value.getplatformdiscountgoods";
    public static final String VALUE_GET_PLATFORM_ACTIVITIES = "value.getplatformactivities";
    public static final String VALUE_GET_BANNERS = "value.getbanners";
    public static final String VALUE_GET_FREEORDERS = "value.freeorders";
    public static final String VALUE_SUPERGROUP_PEOPLE = "value.supergroup_people";
    public static final String VALUE_GET_LOTTERY_DETAILS = "value.getlotterydetails";
    public static final String VALUE_GET_PLATFROMGROUP_DETAILS = "value.getplatformgroupdetails";
    public static final String VALUE_GET_SHOP_DETAILS = "value.getshopdetails";
    public static final String VALUE_SYS_REGIONS = "value.sys.regions";
    public static final String VALUE_SYS_QUESTIONS = "value.sys.questions";
    public static final String VALUE_SYS_SHOP_PROVINCES = "value.sys.shop.provinces";
    public static final String VALUE_SYS_SEARCH_GOODS = "value.sys.search.goods";

    public static final String VALUE_GET_PROMITION_SHOWS = "value.getpromitionshows";


    public static final String VALUE_GIFT_ACTIVITY_PEOPLE = "value.giftactivity_people";
    public static final String VALUE_GIFT_ACTIVITY_PERSONAS_PREX = "value.giftactivity_personas_";



    public static final String VALUE_SYS_REGION_VERSION = "value.sys.region.version";
    public static final String VALUE_SYS_QUESTION_VERSION = "value.sys.question.version";
    public static final String VALUE_GET_ORDER_FREE_CHANCE = "value.orderfreechance"; // no need
    public static final String VALUE_GET_ORDER_FREE_SOLUTION = "value.orderfreesolution";
    public static final String TOKEN_ZXTC_WEIXIN_VALUE = "token.zxtc.weixin_value";
    public static final String TOKEN_ZXTC_WEIXIN_FLAG = "token.zxtc.weixin_flag";
    public static final String TOKEN_ZXTC_TXOSS_VALUE = "token.zxtc.tx_oss_value";
    public static final String TOKEN_ZXTC_TXOSS_FLAG = "token.zxtc.tx_oss_flag";


    public static final String TOKEN_ZXTC_QINIU_VALUE = "token.zxtc.qiniu_value";
    public static final String TOKEN_ZXTC_QINIU_FLAG = "token.zxtc.qiniu__oss_flag";





    public static final String ARTICLE_READ_PREFIX = "article.read.prefix_";

    public static final int EXPIRE_REFUND_FAIL = 3; // HOUR

    public static final int EXPIRE_SESSION_TIME =  30*365;; // DAY

}
