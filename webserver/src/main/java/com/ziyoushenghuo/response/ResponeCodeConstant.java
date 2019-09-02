package com.ziyoushenghuo.response;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


public final class ResponeCodeConstant {

    private ResponeCodeConstant(){
    }

    //exchange name



    public static final String OK_STR = "OK";

    public static final int DEVICEID_NOTFOUND = 1000;
    public static final String DEVICEID_NOTFOUND_STR = "ID ERR OR NOT ADD IN SYSTEM";

    public static final int SESSION_NOTFOUND = 1001;
    public static final String SESSION_NOTFOUND_STR = "no login info found";


    public static final int NOT_PERMIT_INFO = 1002;
    public static final String NOT_PERMIT_INFO_STR = "你没有获取该信息的权限";



    public static final int MEG_NOT_FOUND = 1003;
    public static final String MEG_NOT_FOUND_STR = "您查找的信息已失效或者被删除";

    public static final int WEIXIN_GENERATE_TOKEN_FAIL = 1004;
    public static final String WEIXIN_GENERATE_TOKEN_FAIL_STR = "生成token出错";

    public static final int WEIXIN_ONLOGIN_RETRUN_FAIL = 1005;
    public static final String WEIXIN_ONLOGIN_RETRUN_FAIL_STR = "生成token出错";

    public static final int EXPRESS_GETMESSAGE_FAIL = 1006;
    public static final String EXPRESS_GETMESSAGE_FAIL_STR = "获取快递信息失败";


    public static final int EXCEPTION_OCCUR = 1007;

    public static final int OWNER_REPEAT_BIND = 1008;

    public static final String OWNER_REPEAT_BIND_STR = "该用户已经绑定微信小程序";

    public static final int USER_NOT_CORRECT = 1009;

    public static final String USER_NOT_CORRECT_STR = "用户不存在或密码错误";


    public static final int WEIXIN_ADDR_NOT_EXISTS = 1010;

    public static final String WEIXIN_ADDR_NOT_EXISTS_STR = "微信地址未被系统收录";


    public static final int ORDER_EXPRSS_FEE_FAIL = 1011;
    public static final String ORDER_EXPRSS_FEE_FAIL_STR = "订单物流费不匹配";


    public static final int ORDER_GIFT_BIND_FAIL = 1012;
    public static final String ORDER_GIFT_BIND_FAIL_STR = "礼物绑定失败";

    public static final int ORDER_GIFT_TOKEN_FAIL = 1013;
    public static final String ORDER_GIFT_TOKEN_FAIL_STR = "没有领取权限";

    public static final int ORDER_GIFT_OVER_FAIL = 1014;
    public static final String ORDER_GIFT_OVER_FAIL_STR = "礼物已失效";

    public static final int WITHDREW_CHECKFAIL = 1015;
    public static final String WITHDREW_CHECKFAIL_STR = "提现金额校验失败";


    public static final int PARAM_ERROR = 1016;
    public static final String PARAM_ERROR_STR = "输入参数有误";

    public static final int ORDER_GIFT_STATUS_ERR = 1017;
    public static final String ORDER_GIFT_STATUS_ERR_STR = "活动尚未开始或已结束";

    public static final int ORDER_GIFT_ALL_SEND = 1018;
    public static final String ORDER_GIFT_ALL_SEND_STR = "活动尚未开始或已结束";


    public static final int ORDER_GIFT_ALREADY_IN = 1019;
    public static final String ORDER_GIFT_ALREADY_IN_STR = "已经参加或者预约";





    public static final int GROUP_NOT_FOUND = 1020;
    public static final String GROUP_NOT_FOUND_STR = "您查找的团购信息不存在";


    public static final int GROUP_NOT_VALID = 1021;
    public static final String GROUP_NOT_VALID_STR = "您参与的团购失效或者已结束";


    public static final int RECOMMEND_CODE_NOT_VALID= 1022;
    public static final String RECOMMEND_CODE_NOT_VALID_STR = "激活码未找到或已失效";

    public static final int ORDER_RELAY_STATUS_ERR = 1023;
    public static final String ORDER_RELAY_STATUS_ERR_STR = "助力目标不存在或已中奖";

    public static final int USER_SHARE_MSG_GENERATE_ERR = 1024;
    public static final String USER_SHARE_MSG_GENERATE_ERR_STR = "生成用户分享信息失败";

    public static final int WEIXIN_PAY_FAIL  = 5000;
    public static final String WEIXIN_PAY_FAIL_STR = "支付返回结果失败";

    public static final int WEIXIN_PAY_REPEAT  = 5001;
    public static final String WEIXIN_PAY_REPEAT_STR = "订单重复支付";

    public static final int WEIXIN_REFUND_CHECKFAIL  = 5002;
    public static final String WEIXIN_REFUND_CHECKFAIL_STR = "退款金额有误";

    public static final int WEIXIN_REFUND_NOFOUND  = 5003;
    public static final String WEIXIN_REFUND_NOFOUND_STR = "没有找到对应充值订单";

}
