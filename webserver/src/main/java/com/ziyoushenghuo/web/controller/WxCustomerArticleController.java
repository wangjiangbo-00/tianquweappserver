package com.ziyoushenghuo.web.controller;


import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.util.Auth;
import com.ziyoushenghuo.HttpUtils.HttpUtils;
import com.ziyoushenghuo.alisms.AliSmsUtils;
import com.ziyoushenghuo.asyntask.QiniuTask;
import com.ziyoushenghuo.asyntask.WxOssTask;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.rabbitmq.MQConstant;

import com.ziyoushenghuo.rabbitmq.delay.*;

import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.repository.CustomerArticleRepository;
import com.ziyoushenghuo.repository.OrderExpressRepository;
import com.ziyoushenghuo.repository.OrderExtraRepository;
import com.ziyoushenghuo.response.*;
import com.ziyoushenghuo.service.*;
import com.ziyoushenghuo.entry.*;

import com.ziyoushenghuo.weixinpay.WxPayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ziyoushenghuo.common.Md5Util;

import javax.servlet.http.HttpServletRequest;


/**
 * Created by RXJ on 2017/4/7.
 */
@Controller
@RequestMapping({"/ziyoutechan/customer"})
public class WxCustomerArticleController {

    private final int SESSION_TIME = 30 * 365;
    private final String WEI_APPID = "wxdd41e6a63c02c5f0";
    private final String WEI_APPKEY = "31b051b9b9a347a3906502bf27f3bd2c";

    private final String SESSION_SPLIT = "__";
    private static Logger logger = LoggerFactory.getLogger(WxCustomerController.class);

    @Autowired
    private OrderService orderService;


    @Autowired
    private CustomerService customerService;


    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CustomerAccountService customerAccountService;
    @Autowired
    private OrderExtraRepository orderExtraRepository;
    @Autowired
    private CustomerWithdrewService customerWithdrewService;

    @Autowired
    private CustomerArticleService customerArticleService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private QiniuTask qiniuTask;


    @Autowired
    private WxOssTask wxOssTask;

    @RequestMapping({"/getshoparticles"})
    @ResponseBody
    ResponseContainer getshoparticles(int shopid, int offset, int size) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            List<CustomerArticleShort> customerArticleShorts = customerArticleService.GetShopArticles(shopid, offset, size);
            responseContainer.setLists(customerArticleShorts);

        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
                responseContainer.setErrormsg(e.getMessage().substring(0, len));
                logger.warn(e.getMessage().substring(0, len));
            } else {
                responseContainer.setErrormsg(e.toString());
                logger.warn(e.toString());
            }
        }


        return responseContainer;
    }


    @RequestMapping({"/getgoodsarticles"})
    @ResponseBody
    ResponseContainer getgoodsarticles(int goodsid, int offset, int size) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            List<CustomerArticleShort> customerArticleShorts = customerArticleService.GetGoodsArticles(goodsid, offset, size);
            responseContainer.setLists(customerArticleShorts);

        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
                responseContainer.setErrormsg(e.getMessage().substring(0, len));
                logger.warn(e.getMessage().substring(0, len));
            } else {
                responseContainer.setErrormsg(e.toString());
                logger.warn(e.toString());
            }
        }


        return responseContainer;
    }


    @RequestMapping({"/getcustomerarticles"})
    @ResponseBody
    ResponseContainer getcustomerarticles(String session, int offset, int size) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                List<CustomerArticleShort> customerArticleShorts = customerArticleService.GetCustomerArticles(userid, offset, size);
                responseContainer.setLists(customerArticleShorts);
            } else {
                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
            }
        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
                responseContainer.setErrormsg(e.getMessage().substring(0, len));
                logger.warn(e.getMessage().substring(0, len));
            } else {
                responseContainer.setErrormsg(e.toString());
                logger.warn(e.toString());
            }
        }


        return responseContainer;
    }


    @RequestMapping({"/getarticle"})
    @ResponseBody
    ResponseContainer getarticle(String session, int articleid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            CustomerArticle customerArticle = customerArticleService.GetById(articleid);
            responseContainer.setMessage(customerArticle);

        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
                responseContainer.setErrormsg(e.getMessage().substring(0, len));
                logger.warn(e.getMessage().substring(0, len));
            } else {
                responseContainer.setErrormsg(e.toString());
                logger.warn(e.toString());
            }
        }


        return responseContainer;
    }


    @RequestMapping({"/addarticle"})
    @ResponseBody
    ResponseContainer addarticle(String session, CustomerArticle customerArticle) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();


                customerArticle.setCid(userid);
                customerArticle.setModifytime(new Date());
                customerArticle.setHeadpic(customer.getHeadpic());
                customerArticle.setNikename(customer.getNickname());
                customerArticleService.CreateOrUpdate(customerArticle);
                long orderid = customerArticle.getOrderid();
                if (orderid > 0) {
                    OrderExtra orderExtra = orderExtraRepository.findById(orderid);

                    if (orderExtra != null) {
                        orderExtra.setArticleid(customerArticle.getId());
                        orderExtraRepository.save(orderExtra);
                    }

                    Order order = orderService.getOrderById(orderid);
                    if (order != null) {
                        order.setOrderStatus(Order.ORDER_STATUS_FINISH);
                        order.setFinishreason(Order.ORDER_FINISH_REASON_NORMAL);
                        orderService.UpdateOrder(order);
                    }
                }


            } else {
                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
            }
        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
                responseContainer.setErrormsg(e.getMessage().substring(0, len));
                logger.warn(e.getMessage().substring(0, len));
            } else {
                responseContainer.setErrormsg(e.toString());
                logger.warn(e.toString());
            }
        }


        return responseContainer;
    }


    @RequestMapping({"/addread"})
    @ResponseBody
    ResponseContainer addread(String session, int articleid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                CustomerArticleShort customerArticleShort = customerArticleService.GetShortById(articleid);

                customerArticleShort.setReadcount(customerArticleShort.getReadcount() + 1);

                customerArticleService.CreateOrUpdate(customerArticleShort);

            } else {
                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
            }
        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
                responseContainer.setErrormsg(e.getMessage().substring(0, len));
                logger.warn(e.getMessage().substring(0, len));
            } else {
                responseContainer.setErrormsg(e.toString());
                logger.warn(e.toString());
            }
        }


        return responseContainer;
    }


    @RequestMapping({"/setarticlepublic"})
    @ResponseBody
    ResponseContainer setarticlepublic(String session, int articleid, int ispublic) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {

                CustomerArticleShort customerArticleShort = customerArticleService.GetShortById(articleid);

                customerArticleShort.setIspublic(ispublic);

                customerArticleService.CreateOrUpdate(customerArticleShort);

            } else {
                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
            }
        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
                responseContainer.setErrormsg(e.getMessage().substring(0, len));
                logger.warn(e.getMessage().substring(0, len));
            } else {
                responseContainer.setErrormsg(e.toString());
                logger.warn(e.toString());
            }
        }


        return responseContainer;
    }


    @RequestMapping({"/setarticlebgstyle"})
    @ResponseBody
    ResponseContainer setarticlebgstyle(String session, int articleid, int style) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {

                CustomerArticleShort customerArticleShort = customerArticleService.GetShortById(articleid);

                customerArticleShort.setBgstyle(style);

                customerArticleService.CreateOrUpdate(customerArticleShort);

            } else {
                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
            }
        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
                responseContainer.setErrormsg(e.getMessage().substring(0, len));
                logger.warn(e.getMessage().substring(0, len));
            } else {
                responseContainer.setErrormsg(e.toString());
                logger.warn(e.toString());
            }
        }


        return responseContainer;
    }

    static class ArticleContent {
        int type;
        String content;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    @RequestMapping({"/deletearticle"})
    @ResponseBody
    ResponseContainer deletearticle(String session, int articleid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();
                CustomerArticle customerArticle = customerArticleService.GetById(articleid);

                List<String> pics = new ArrayList<>();

                String contents = customerArticle.getContent();
                if (contents != null) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<ArticleContent>>() {
                    }.getType();
                    List<ArticleContent> articleContents = gson.fromJson(contents, type);

                    for (ArticleContent articleContent : articleContents) {
                        if (articleContent.getType() == 2) {
                            String pic = articleContent.getContent();
                            pic = pic.substring(pic.indexOf("com") + 4, pic.length());
                            pics.add(pic);
                        }
                    }


                }

                String coverpic = customerArticle.getCoverpic();
                if (coverpic != null) {
                    String pic = coverpic;
                    pic = pic.substring(pic.indexOf("com") + 4, pic.length());
                    pics.add(pic);
                }

                qiniuTask.deletepics(pics);


                if (customerArticle.getCid() == userid) {
                    customerArticleService.Delete(customerArticle);
                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.NOT_PERMIT_INFO);
                    responseContainer.setErrormsg(ResponeCodeConstant.NOT_PERMIT_INFO_STR);
                }


            } else {
                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
            }
        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
                responseContainer.setErrormsg(e.getMessage().substring(0, len));
                logger.warn(e.getMessage().substring(0, len));
            } else {
                responseContainer.setErrormsg(e.toString());
                logger.warn(e.toString());
            }
        }


        return responseContainer;
    }

    @RequestMapping({"/setarticlecollect"})
    @ResponseBody
    ResponseContainer setarticlecollect(String session, int articleid, int action) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                CustomerArticleShort customerArticleShort = customerArticleService.GetShortById(articleid);
                if (customer != null && customerArticleShort != null) {
                    String articlecollect = customer.getArticlecollect();

                    if (articlecollect != null && !articlecollect.isEmpty()) {
                        if (Customer.ACTION_SET == action) {
                            articlecollect = String.valueOf(articleid) + "," + articlecollect;

                            customer.setArticlecollect(articlecollect);
                            customerService.update(customer);

                            customerArticleShort.setCollect(customerArticleShort.getCollect() + 1);

                            customerArticleService.CreateOrUpdate(customerArticleShort);
                        } else {
                            String articlewithcomma = articleid + ",";
                            String articletr = articleid + "";

                            StringBuffer stringBuffer = new StringBuffer();
                            String articlecollectafter = StringUtils.remove(articlecollect, articlewithcomma);

                            if (articlecollectafter.length() == articlecollect.length()) {
                                articlecollectafter = StringUtils.remove(articlecollect, articletr);
                            }

                            customer.setArticlecollect(articlecollectafter);
                            customerService.update(customer);

                            customerArticleShort.setCollect(customerArticleShort.getCollect() - 1);

                            customerArticleService.CreateOrUpdate(customerArticleShort);

                        }
                    } else {
                        if (Customer.ACTION_SET == action) {
                            articlecollect = String.valueOf(articleid);
                            customer.setArticlecollect(articlecollect);

                            customerService.update(customer);
                        }
                    }


                }
            } else {
                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
            }
        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
                responseContainer.setErrormsg(e.getMessage().substring(0, len));
                logger.warn(e.getMessage().substring(0, len));
            } else {
                responseContainer.setErrormsg(e.toString());
                logger.warn(e.toString());
            }
        }


        return responseContainer;
    }


    @RequestMapping({"/getarticlecollect"})
    @ResponseBody
    ResponseContainer getarticlecollect(String session) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {

                String articlecollect = customer.getArticlecollect();
                List<CustomerArticleShort> articles = new ArrayList<CustomerArticleShort>();
                if (articlecollect != null && !articlecollect.isEmpty()) {
                    String[] articlestrs = articlecollect.split(",");


                    List<Integer> articleids = new ArrayList<>();
                    for (String articlestr : articlestrs) {
                        int articleid = Integer.valueOf(articlestr);

                        articleids.add(articleid);

                    }

                    articles = customerArticleService.GetArticlesByIds(articleids);
                }
                responseContainer.setLists(articles);

            } else {
                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
            }
        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
                responseContainer.setErrormsg(e.getMessage().substring(0, len));
                logger.warn(e.getMessage().substring(0, len));
            } else {
                responseContainer.setErrormsg(e.toString());
                logger.warn(e.toString());
            }
        }


        return responseContainer;
    }


    @RequestMapping({"/updatearticle"})
    @ResponseBody
    ResponseContainer updatearticle(String session, CustomerArticle customerArticle) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                CustomerArticle article = customerArticleService.GetById(customerArticle.getId());

                article.setModifytime(new Date());

                article.setTitle(customerArticle.getTitle());

                article.setCoverpic(customerArticle.getCoverpic());

                article.setContent(customerArticle.getContent());


                customerArticleService.CreateOrUpdate(article);


            } else {
                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
            }
        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
                responseContainer.setErrormsg(e.getMessage().substring(0, len));
                logger.warn(e.getMessage().substring(0, len));
            } else {
                responseContainer.setErrormsg(e.toString());
                logger.warn(e.toString());
            }
        }


        return responseContainer;
    }


    @RequestMapping({"/deleteqiniupics"})
    @ResponseBody
    ResponseContainer deleteqiniupics(String session, String[] pics) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                List<String> keys = new ArrayList<String>(pics.length);
                Collections.addAll(keys, pics);
                qiniuTask.deletepics(keys);

            } else {
                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
            }
        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
                responseContainer.setErrormsg(e.getMessage().substring(0, len));
                logger.warn(e.getMessage().substring(0, len));
            } else {
                responseContainer.setErrormsg(e.toString());
                logger.warn(e.toString());
            }
        }


        return responseContainer;
    }


    @RequestMapping({"/deletewxosspics"})
    @ResponseBody
    ResponseContainer deletewxosspics(String session, String[] pics) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                List<String> keys = new ArrayList<String>(pics.length);
                Collections.addAll(keys, pics);
                wxOssTask.deletepics(keys);

            } else {
                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
            }
        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
                responseContainer.setErrormsg(e.getMessage().substring(0, len));
                logger.warn(e.getMessage().substring(0, len));
            } else {
                responseContainer.setErrormsg(e.toString());
                logger.warn(e.toString());
            }
        }


        return responseContainer;
    }


    @RequestMapping({"/deletedraftpics"})
    @ResponseBody
    ResponseContainer deletedraftpics(String session, int articleid, String cover, String[] pics) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                CustomerArticle article = customerArticleService.GetById(articleid);

                if (article != null) {
                    ArrayList<String> keys = new ArrayList<>();
                    if (!cover.equals(article.getCoverpic())) {
                        String pic = cover;
                        pic = pic.substring(pic.indexOf("com") + 4, pic.length());

                        keys.add(pic);
                    }

                    String contents = article.getContent();

                    if (contents != null) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<ArticleContent>>() {
                        }.getType();
                        List<ArticleContent> articleContents = gson.fromJson(contents, type);

                        for (String pic : pics) {
                            boolean bfind = false;

                            for (ArticleContent articleContent : articleContents) {
                                if (articleContent.getType() == 2 && articleContent.getContent().equals(pic)) {
                                    bfind = true;
                                    break;

                                }
                            }

                            if (!bfind) {
                                String key = pic;
                                key = key.substring(key.indexOf("com") + 4, key.length());
                                keys.add(key);
                            }


                        }
                    }

                    if (!keys.isEmpty()) {
                        qiniuTask.deletepics(keys);
                    }


                } else {
                    List<String> keys = new ArrayList<String>(pics.length);
                    Collections.addAll(keys, pics);

                    wxOssTask.deletepics(keys);
                }

            } else {
                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
            }
        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
                responseContainer.setErrormsg(e.getMessage().substring(0, len));
                logger.warn(e.getMessage().substring(0, len));
            } else {
                responseContainer.setErrormsg(e.toString());
                logger.warn(e.toString());
            }
        }


        return responseContainer;
    }


}
