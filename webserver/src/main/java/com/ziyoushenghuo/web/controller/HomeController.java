package com.ziyoushenghuo.web.controller;


import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.util.Auth;
import com.ziyoushenghuo.alisms.AliSmsUtils;
import com.ziyoushenghuo.common.ImageHandleHelper;
import com.ziyoushenghuo.entry.*;

import com.ziyoushenghuo.express.KdniaoSubscribeAPI;
import com.ziyoushenghuo.express.KdniaoTrackQueryAPI;
import com.ziyoushenghuo.rabbitmq.MQConstant;

import com.ziyoushenghuo.rabbitmq.delay.DelayUnGroupSender;

import com.ziyoushenghuo.scheduling.FlagCheckOrderExpress;
import com.ziyoushenghuo.service.*;

import com.ziyoushenghuo.weixinpay.WxPayUtils;
import com.ziyoushenghuo.weixintoken.WxTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.*;
import java.util.Date;
import java.util.List;

import java.util.Properties;

/**
 * Created by RXJ on 2017/4/7.
 */
@Controller
@RequestMapping({"/ziyoutechan"})
public class HomeController {





}
