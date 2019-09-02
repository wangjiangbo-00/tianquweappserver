package com.ziyoushenghuo.web.controller;


import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.ziyoushenghuo.HttpUtils.HttpUtils;
import com.ziyoushenghuo.common.*;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.repository.*;
import com.ziyoushenghuo.response.*;
import com.ziyoushenghuo.service.*;
import com.ziyoushenghuo.entry.*;

import com.ziyoushenghuo.weixinpay.WxPayConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by RXJ on 2017/4/7.
 */
@Controller
@RequestMapping({"/ziyoutechan/customer"})
public class WxCustomerController {

    private final int SESSION_TIME = 30 * 365;


    private final String SESSION_SPLIT = "__";
    private static Logger logger = LoggerFactory.getLogger(WxCustomerController.class);





    @Autowired
    private GoodsSkuService goodsSkuService;

    @Autowired
    private CustomerService customerService;


    @Autowired
    private ShopService shopService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private TeamFounderService teamFounderService;

    @Autowired
    private GoodsImageService goodsImageService;

    @Autowired
    private GoodsCategoryService goodsCategoryService;

    @Autowired
    private CustomerShipAddrService customerShipAddrService;

    @Autowired
    private ShopExpressFeeService shopExpressFeeService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private PlatformActivityRepository platformActivityRepository;


    @Autowired
    private PlatformBannerRepository platformBannerRepository;

    @Autowired
    private PromotionDiscountGoodsRepository promotionDiscountGoodsRepository;

    @Autowired
    private SuperGroupAppointmentRepository superGroupAppointmentRepository;


    @Autowired
    private SystemNoticeRepository systemNoticeRepository;

    @Autowired
    private ShopPickupPointRepository shopPickupPointRepository;


    @RequestMapping({"/setaddrdefault"})
    @ResponseBody
    ResponseContainer setaddrdefault(String session, int address_id) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();
                CustomerShipAddr customerShipAddr = customerShipAddrService.GetByid(address_id);

                List<CustomerShipAddr> customerShipAddrs = customerShipAddrService.GetUserDefault(userid);
                if (customerShipAddrs != null) {
                    for (CustomerShipAddr customerShipAddr1 : customerShipAddrs) {
                        customerShipAddr1.setIsdefault(0);
                        customerShipAddrService.update(customerShipAddr1);
                    }

                }
                if (customerShipAddr == null) {
                    responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
                } else {
                    if (customerShipAddr.getUid() == userid) {
                        customerShipAddr.setIsdefault(1);

                        customerShipAddrService.update(customerShipAddr);

                    } else {
                        responseContainer.setReturncode(ResponeCodeConstant.NOT_PERMIT_INFO);
                        responseContainer.setErrormsg(ResponeCodeConstant.NOT_PERMIT_INFO_STR);
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

    @RequestMapping({"/setshopcollect"})
    @ResponseBody
    ResponseContainer setshopcollect(String session, int shopid, int action) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                String shopcollect = customer.getShopcollect();

                if (shopcollect != null && !shopcollect.isEmpty()) {

                    if (Customer.ACTION_SET == action) {
                        shopcollect = String.valueOf(shopid) + "," + shopcollect;

                        customer.setShopcollect(shopcollect);
                        customerService.update(customer);
                    } else {
                        String shopwithcomma = shopid + ",";
                        String shoptr = shopid + "";

                        StringBuffer stringBuffer = new StringBuffer();
                        String shopcollectafter = StringUtils.remove(shopcollect, shopwithcomma);

                        if (shopcollectafter.length() == shopcollect.length()) {
                            shopcollectafter = StringUtils.remove(shopcollect, shoptr);
                        }

                        customer.setShopcollect(shopcollectafter);
                        customerService.update(customer);

                    }
                } else {
                    if (Customer.ACTION_SET == action) {
                        shopcollect = String.valueOf(shopid);
                        customer.setShopcollect(shopcollect);

                        customerService.update(customer);
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




    @RequestMapping({"/setuserconfigflag"})
    @ResponseBody
    ResponseContainer setuserconfigflag(String session, int flagbit, int value) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {

                int configflag= customer.getConfigflag();
                if(value == 0)
                {
                    configflag = configflag & (~(1 << flagbit));
                }
                else
                {
                    configflag = configflag | (1 << flagbit);
                }

                customer.setConfigflag(configflag);

                customerService.update(customer);



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



    @RequestMapping({"/getrecommendpersons"})
    @ResponseBody
    ResponseContainer getrecommendpersons(String session, int offset, int size) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {

                List<Customer> customerList = customerService.GetMyRecommendPersons(customer.getId(),offset,size);

                responseContainer.setLists(customerList);

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


    @RequestMapping({"/getspreadname"})
    @ResponseBody
    ResponseContainer getspreadname(String session, int userid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {

                Customer customer1 = customerService.GetNameIdById(userid);

                if(customer1!=null)
                {
                    responseContainer.setExtramsg(customer1.getNickname());
                }
                else
                {
                    responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
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


    @RequestMapping({"/getshoppickuppoint"})
    @ResponseBody
    ResponseContainer getshoppickuppoint(String session, int shopid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {


                ShopPickupPoint shopPickupPoint = shopPickupPointRepository.findByShopid(shopid);

                responseContainer.setMessage(shopPickupPoint);



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

    @RequestMapping({"/getshopcollect"})
    @ResponseBody
    ResponseContainer getshopcollect(String session) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                String shopcollect = customer.getShopcollect();
                List<Shop> shops = new ArrayList<Shop>();
                if (shopcollect != null && !shopcollect.isEmpty()) {
                    String[] shopstrs = shopcollect.split(",");


                    for (String shopstr : shopstrs) {
                        int shopid = Integer.valueOf(shopstr);

                        String shopsdetails = (String) redisUtils.hmGet(RedisKeyConstant.VALUE_GET_SHOP_DETAILS, String.valueOf(shopid));
                        if (shopsdetails != null) {
                            Type type = new TypeToken<Shop>() {
                            }.getType();
                            Gson gson = new Gson();

                            Shop shop = gson.fromJson(shopsdetails, type);

                            shops.add(shop);

                        } else {
                            Shop shop = shopService.GetById(shopid);
                            if (shop != null) {


                                Gson gson = new Gson();
                                String jsonstr = gson.toJson(shop);
                                redisUtils.hmSet(RedisKeyConstant.VALUE_GET_SHOP_DETAILS, String.valueOf(shopid), jsonstr);
                                shops.add(shop);

                            }
                        }
                    }


                }
                responseContainer.setLists(shops);

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


    @RequestMapping({"/setgoodscollect"})
    @ResponseBody
    ResponseContainer setgoodscollect(String session, int goodsid, int action) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                String goodcollect = customer.getGoodscollect();

                if (goodcollect != null && !goodcollect.isEmpty()) {
                    if (Customer.ACTION_SET == action) {
                        goodcollect = String.valueOf(goodsid) + "," + goodcollect;

                        customer.setGoodscollect(goodcollect);
                        customerService.update(customer);
                    } else {
                        String goodwithcomma = goodsid + ",";
                        String goodstr = goodsid + "";

                        StringBuffer stringBuffer = new StringBuffer();
                        String goodcollectafter = StringUtils.remove(goodcollect, goodwithcomma);

                        if (goodcollectafter.length() == goodcollect.length()) {
                            goodcollectafter = StringUtils.remove(goodcollect, goodstr);
                        }

                        customer.setGoodscollect(goodcollectafter);
                        customerService.update(customer);

                    }
                } else {
                    if (Customer.ACTION_SET == action) {
                        goodcollect = String.valueOf(goodsid);
                        customer.setGoodscollect(goodcollect);
                        customerService.update(customer);
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

    @RequestMapping({"/getgoodscollect"})
    @ResponseBody
    ResponseContainer getgoodscollect(String session) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                String goodcollect = customer.getGoodscollect();
                List<Goods> goodsList = new ArrayList<Goods>();
                if (goodcollect != null && !goodcollect.isEmpty()) {
                    String[] goodsstrs = goodcollect.split(",");


                    for (String goodsstr : goodsstrs) {
                        int goodsid = Integer.valueOf(goodsstr);

                        String goodsdetails = (String) redisUtils.hmGet(RedisKeyConstant.VALUE_GET_GOODS_DETAILS, String.valueOf(goodsid));
                        if (goodsdetails != null) {
                            Type type = new TypeToken<Goods>() {
                            }.getType();
                            Gson gson = new Gson();

                            Goods goods = gson.fromJson(goodsdetails, type);

                            goodsList.add(goods);

                        } else {
                            Goods goods = goodsService.getById(goodsid);
                            if (goods != null) {


                                Gson gson = new Gson();
                                String jsonstr = gson.toJson(goods);
                                redisUtils.hmSet(RedisKeyConstant.VALUE_GET_GOODS_DETAILS, String.valueOf(goodsid), jsonstr);
                                goodsList.add(goods);

                            }
                        }
                    }


                }
                responseContainer.setLists(goodsList);

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


    @RequestMapping({"/getrecommendshops"})
    @ResponseBody
    ResponseContainer getrecommendshops(String session) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();

            String shopkey = RedisKeyConstant.VALUE_GET_RECOMMEND_SHOPS;


            String redisshops = operations.get(shopkey);

            if (redisshops == null) {
                List<Shop> shops = new ArrayList<Shop>();

                shops = shopService.GetAll();


                Gson gson = new Gson();
                String shopjson = gson.toJson(shops);
                operations.set(shopkey, shopjson);
                responseContainer.setLists(shops);
            } else {
                Type type = new TypeToken<ArrayList<Shop>>() {
                }.getType();
                Gson gson = new Gson();

                List<Shop> shops = gson.fromJson(redisshops, type);
                responseContainer.setLists(shops);
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


    @RequestMapping({"/getplatformgroup"})
    @ResponseBody
    ResponseContainer getplatformgroup(String session, int datastatus, int offset, int size) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();

            String key = RedisKeyConstant.VALUE_GET_PLATFORM_GROUP;

            String indexstr = String.valueOf(datastatus) + "_" + String.valueOf(offset);
            String redisstr = (String) redisUtils.hmGet(key, indexstr);

            if (redisstr == null) {
                List<TeamFounder> teamFounders = new ArrayList<TeamFounder>();


                List<Integer> integers = new ArrayList<>();
                if (datastatus == 0) {
                    integers.add(GiftActivity.GIFT_STATUS_START);
                    integers.add(GiftActivity.GIFT_STATUS_CAN_SHOW);

                } else if (datastatus == 1) {
                    integers.add(GiftActivity.GIFT_STATUS_END);
                    integers.add(GiftActivity.GIFT_STATUS_CLOSE);
                }


                List<Sort.Order> orders = new ArrayList<>();
                orders.add(new Sort.Order(Sort.Direction.DESC,"status"));
                orders.add(new Sort.Order(Sort.Direction.DESC,"expiretime"));

                teamFounders = teamFounderService.GetPlatGroups(integers,new Sort(orders),offset,size);

                Gson gson = new Gson();
                String jsonstr = gson.toJson(teamFounders);
                redisUtils.hmSet(key, indexstr, jsonstr);
                responseContainer.setLists(teamFounders);
            } else {
                Type type = new TypeToken<ArrayList<TeamFounder>>() {
                }.getType();
                Gson gson = new Gson();

                List<TeamFounder> teamFounders = gson.fromJson(redisstr, type);
                responseContainer.setLists(teamFounders);
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


    @RequestMapping({"/getplatformgroupdetails"})
    @ResponseBody
    ResponseContainer getplatformgroupdetails(String session, int groupid) {

        ResponseContainer responseContainer = new ResponseContainer();


        Customer customer = customerService.GetByToken(session);

        if (customer != null) {
            int userid = customer.getId();

            SuperGroupDetailsMessage superGroupDetailsMessage = new SuperGroupDetailsMessage();

            try {


                String lotterydetails = (String) redisUtils.hmGet(RedisKeyConstant.VALUE_GET_PLATFROMGROUP_DETAILS, String.valueOf(groupid));

                if (lotterydetails != null) {
                    Type type = new TypeToken<TeamFounder>() {
                    }.getType();
                    Gson gson = new Gson();

                    TeamFounder teamFounder = gson.fromJson(lotterydetails, type);

                    superGroupDetailsMessage.setTeamFounder(teamFounder);

                    String peoplestr = (String) redisUtils.hmGet(RedisKeyConstant.VALUE_SUPERGROUP_PEOPLE, String.valueOf(groupid));

                    int people = teamFounder.getPeople();

                    if (peoplestr != null) {
                        people = Integer.valueOf(peoplestr);
                    } else {

                        List<Integer> integers = teamFounderService.findPeopleById(groupid);

                        if (!integers.isEmpty()) {
                            people = integers.get(0);
                            redisUtils.hmSet(RedisKeyConstant.VALUE_SUPERGROUP_PEOPLE, String.valueOf(groupid), String.valueOf(people));
                        }
                    }


                    superGroupDetailsMessage.setPeople(people);

                } else {
                    TeamFounder teamFounder = teamFounderService.GetById(groupid);
                    if (teamFounder != null) {

                        GsonBuilder b = new GsonBuilder();

                        b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);

                        Gson gson = b.create();

                        String jsonstr = gson.toJson(teamFounder);
                        redisUtils.hmSet(RedisKeyConstant.VALUE_GET_PLATFROMGROUP_DETAILS, String.valueOf(groupid), jsonstr);
                        superGroupDetailsMessage.setTeamFounder(teamFounder);
                        superGroupDetailsMessage.setPeople(teamFounder.getPeople());
                    } else {
                        responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                        responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
                    }
                }

                List<Long> orderids = orderService.findOrderIdByGroupUser(groupid, userid);

                if (!orderids.isEmpty()) {

                    Long orderlong = orderids.get(0);

                    int orderid = orderlong.intValue();
                    superGroupDetailsMessage.setOrderid(orderid);
                } else {
                    superGroupDetailsMessage.setOrderid(0);
                }
            } catch (Exception e) {
                responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
                responseContainer.setErrormsg(e.getMessage().substring(0, len));
                logger.warn(e.getMessage().substring(0, len));
            }


            responseContainer.setMessage(superGroupDetailsMessage);
        } else {
            responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
            responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
        }

        return responseContainer;
    }

    @RequestMapping({"/getsupergroupappointment"})
    @ResponseBody
    ResponseContainer getsupergroupappointment(String session, int groupid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();
                SuperGroupAppointment superGroupAppointment = superGroupAppointmentRepository.findByGroupidAndUserid(groupid, userid);


                responseContainer.setMessage(superGroupAppointment);

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

    @RequestMapping({"/supergroupappointment"})
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    ResponseContainer supergroupappointment(String session, int groupid, int action, String formid) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                TeamFounder teamFounder = teamFounderService.GetById(groupid);

                if (teamFounder == null) {
                    responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
                    return responseContainer;
                }

                if (teamFounder.getStatus() != GiftActivity.GIFT_STATUS_CAN_SHOW) {
                    // todo check cheat
                    responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_STATUS_ERR);
                    responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_STATUS_ERR_STR);
                    return responseContainer;
                }


                SuperGroupAppointment superGroupAppointment = superGroupAppointmentRepository.findByGroupidAndUserid(groupid, userid);


                if (superGroupAppointment != null) {
                    superGroupAppointment.setStatus(action);
                    superGroupAppointment.setFormid(formid);

                    superGroupAppointmentRepository.save(superGroupAppointment);
                } else {
                    superGroupAppointment = new SuperGroupAppointment();


                    superGroupAppointment.setFormid(formid);
                    superGroupAppointment.setGroupid(groupid);
                    superGroupAppointment.setUserid(userid);
                    superGroupAppointment.setStatus(action);

                    superGroupAppointmentRepository.save(superGroupAppointment);
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


    @RequestMapping({"/getplatformdiscountgoods"})
    @ResponseBody
    ResponseContainer getplatformdiscountgoods(String session, int pid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            String key = RedisKeyConstant.VALUE_GET_PLATFORM_DISCOUNT_GOODS;

            String redisstr = (String) redisUtils.hmGet(key, String.valueOf(pid));


            if (redisstr == null) {
                List<PromotionDiscountGoods> promotionDiscountGoods = new ArrayList<PromotionDiscountGoods>();


                List<Integer> integers = new ArrayList<Integer>();

                integers.add(PromotionDiscount.STATUS_START);
                integers.add(PromotionDiscount.STATUS_CAN_SHOW);

                promotionDiscountGoods = promotionDiscountGoodsRepository.findAllByPidAndStatusIn(pid,integers);

                Gson gson = new Gson();
                String jsonstr = gson.toJson(promotionDiscountGoods);
                redisUtils.hmSet(key, String.valueOf(pid), jsonstr);
                responseContainer.setLists(promotionDiscountGoods);
            } else {
                Type type = new TypeToken<ArrayList<PromotionDiscountGoods>>() {
                }.getType();
                Gson gson = new Gson();

                List<PromotionDiscountGoods> teamFounders = gson.fromJson(redisstr, type);
                responseContainer.setLists(teamFounders);
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


    @RequestMapping({"/getplatformactivitiyinfo"})
    @ResponseBody
    ResponseContainer getplatformactivitiyinfo(String session,int pid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

                List<PlatformActivity> platformActivities = new ArrayList<PlatformActivity>();


            PlatformActivity platformActivity = platformActivityRepository.findByDiscountid(pid);



                responseContainer.setMessage(platformActivity);

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


    @RequestMapping({"/getplatformactivities"})
    @ResponseBody
    ResponseContainer getplatformactivities(String session) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();

            String key = RedisKeyConstant.VALUE_GET_PLATFORM_ACTIVITIES;


            String redisstr = operations.get(key);

            if (redisstr == null) {
                List<PlatformActivity> platformActivities = new ArrayList<PlatformActivity>();


                platformActivities = platformActivityRepository.findAllByIsvisible(1);


                Gson gson = new Gson();
                String jsonstr = gson.toJson(platformActivities);
                operations.set(key, jsonstr);
                responseContainer.setLists(platformActivities);
            } else {
                Type type = new TypeToken<ArrayList<PlatformActivity>>() {
                }.getType();
                Gson gson = new Gson();

                List<PlatformActivity> teamFounders = gson.fromJson(redisstr, type);
                responseContainer.setLists(teamFounders);
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


    @RequestMapping({"/getbanners"})
    @ResponseBody
    ResponseContainer getbanners(String session) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();

            String key = RedisKeyConstant.VALUE_GET_BANNERS;


            String redisstr = operations.get(key);

            if (redisstr == null) {
                List<PlatformBanner> platformBanners = new ArrayList<PlatformBanner>();


                platformBanners = platformBannerRepository.findAllByIsshowAndIsvalid(1, 1);


                Gson gson = new Gson();
                String jsonstr = gson.toJson(platformBanners);
                operations.set(key, jsonstr);
                responseContainer.setLists(platformBanners);
            } else {
                Type type = new TypeToken<ArrayList<PlatformBanner>>() {
                }.getType();
                Gson gson = new Gson();

                List<PlatformBanner> teamFounders = gson.fromJson(redisstr, type);
                responseContainer.setLists(teamFounders);
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


    @RequestMapping({"/getgoodsdetails"})
    @ResponseBody
    ResponseContainer getgoodsdetails(String session, int goodsid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            String goodsdetails = (String) redisUtils.hmGet(RedisKeyConstant.VALUE_GET_GOODS_DETAILS_V1, String.valueOf(goodsid));
            GoodsDetailsMessage goodsDetailsMessage = null;
            if (goodsdetails != null) {
                Type type = new TypeToken<GoodsDetailsMessage>() {
                }.getType();
                Gson gson = new Gson();

                goodsDetailsMessage = gson.fromJson(goodsdetails, type);

                responseContainer.setMessage(goodsDetailsMessage);

            } else {

                Goods goods = goodsService.getById(goodsid);
                if (goods != null) {
                    goodsDetailsMessage = new GoodsDetailsMessage();
                    List<String> gallery = new ArrayList<String>();
                    if (goods != null) {
                        String imagesStr = goods.getImgarr();

                        String[] imagearr = imagesStr.split(",");


                        for (String imgid : imagearr) {
                            GoodImage goodImage = goodsImageService.getById(Integer.valueOf(imgid));
                            if (goodImage != null) {
                                gallery.add(goodImage.getImageurl());
                            }
                        }
                        goodsDetailsMessage.setGallery(gallery);
                        goodsDetailsMessage.setGoods(goods);

                    }

                    List<GoodsSku> goodsSkus = goodsSkuService.getByGoodsid(goodsid);

                    goodsDetailsMessage.setGoodsSkuList(goodsSkus);
                    //GsonBuilder b = new GsonBuilder();

                    // b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);

                    //Gson gson = b.create();

                    Gson gson = new Gson();


                    String jsonstr = gson.toJson(goodsDetailsMessage);
                    redisUtils.hmSet(RedisKeyConstant.VALUE_GET_GOODS_DETAILS_V1, String.valueOf(goodsid), jsonstr);
                    responseContainer.setMessage(goodsDetailsMessage);


                }
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
    /*

    @RequestMapping({"/getgoodsdetails"})
    @ResponseBody
    ResponseContainer getgoodsdetails(String session, int goodsid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            String goodsdetails = (String) redisUtils.hmGet(RedisKeyConstant.VALUE_GET_GOODS_DETAILS, String.valueOf(goodsid));

            if (goodsdetails != null) {
                Type type = new TypeToken<Goods>() {
                }.getType();
                Gson gson = new Gson();

                Goods goods = gson.fromJson(goodsdetails, type);

                responseContainer.setMessage(goods);

            } else {
                Goods goods = goodsService.getById(goodsid);
                if (goods != null) {
                    List<String> gallery = new ArrayList<String>();
                    if (goods != null) {
                        String imagesStr = goods.getImgarr();

                        String[] imagearr = imagesStr.split(",");


                        for (String imgid : imagearr) {
                            GoodImage goodImage = goodsImageService.getById(Integer.valueOf(imgid));
                            if (goodImage != null) {
                                gallery.add(goodImage.getImageurl());
                            }
                        }

                        goods.setGallery(gallery);
                    }

                    List<GoodsSku> goodsSkus = goodsSkuService.getByGoodsid(goodsid);

                    goods.setGoodsSkuList(goodsSkus);
                    GsonBuilder b = new GsonBuilder();

                    b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);

                    Gson gson = b.create();

                    String jsonstr = gson.toJson(goods);
                    redisUtils.hmSet(RedisKeyConstant.VALUE_GET_GOODS_DETAILS, String.valueOf(goodsid), jsonstr);
                    responseContainer.setMessage(goods);


                }
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

 */
    @RequestMapping({"/getrecommendgoods"})
    @ResponseBody
    ResponseContainer getrecommendgoods(String session, int catid, int offset, int size) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            String goodslist = (String) redisUtils.hmGet(RedisKeyConstant.VALUE_GET_RECOMMEND_GOODS, String.valueOf(catid));

            if (goodslist != null) {
                Type type = new TypeToken<List<GoodsCoverInfo>>() {
                }.getType();
                Gson gson = new Gson();

                List<GoodsCoverInfo> goods = gson.fromJson(goodslist, type);

                responseContainer.setLists(goods);
            } else {
                List<GoodsBasic> goods = null;
                if (catid == 0) {
                    goods = goodsService.getRecommendGoods(offset, size);
                } else {
                    goods = goodsService.getRecommendGoodsByCat(catid, offset, size);
                }

                List<GoodsCoverInfo> goodsCoverInfos = new ArrayList<>(goods.size());
                for (GoodsBasic goodsBasic : goods) {
                    GoodsCoverInfo goodsCoverInfo = new GoodsCoverInfo();
                    goodsCoverInfo.setId(goodsBasic.getId());
                    if (goodsBasic.getPictureurl() != null) {
                        goodsCoverInfo.setCover(goodsBasic.getPictureurl());
                    } else {
                        goodsCoverInfo.setCover(null);
                    }
                    goodsCoverInfo.setGroup_number(goodsBasic.getGroup_number());
                    goodsCoverInfo.setIntroduction(goodsBasic.getIntroduction());
                    goodsCoverInfo.setGroup_price(goodsBasic.getGroup_price());
                    goodsCoverInfos.add(goodsCoverInfo);
                }


                if(catid == 0 && goodsCoverInfos.size() == 0)
                {
                    logger.warn("we occor the goods = 0 error here");

                    responseContainer.setLists(goodsCoverInfos);
                }
                else
                {
                    Gson gson = new Gson();
                    String jsonstr = gson.toJson(goodsCoverInfos);
                    redisUtils.hmSet(RedisKeyConstant.VALUE_GET_RECOMMEND_GOODS, String.valueOf(catid), jsonstr);
                    responseContainer.setLists(goodsCoverInfos);
                }




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


    @RequestMapping({"/getgoodsdiscount"})
    @ResponseBody
    ResponseContainer getgoodsdiscount(String session, int goodsid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            GoodsCover goodsCover = new GoodsCover();
            goodsCover.setId(goodsid);
            PromotionDiscountGoods promotionDiscountGoods = promotionDiscountGoodsRepository.findGoodsDiscount(goodsCover);
            responseContainer.setMessage(promotionDiscountGoods);
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

    @RequestMapping({"/preShipFee"})
    @ResponseBody
    ResponseContainer preShipFee(int goodsid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            double expressfee = 0;

            Goods goods = goodsService.getById(goodsid);
            int feeid = goods.getFeeid();
            if (feeid == 0) {

            } else {
                ShopExpressFee shopExpressFee = shopExpressFeeService.getById(feeid);
                if (shopExpressFee != null) {
                    int weight = goods.getWeight();

                    if (weight <= shopExpressFee.getWeight_snum()) {
                        expressfee = shopExpressFee.getWeight_sprice();
                    } else {
                        int xweight = (weight - shopExpressFee.getWeight_snum()) / shopExpressFee.getWeight_xnum();
                        int moreweight = (weight - shopExpressFee.getWeight_snum()) % shopExpressFee.getWeight_xnum() == 0 ? 0 : 1;
                        expressfee = shopExpressFee.getWeight_sprice() +
                                shopExpressFee.getWeight_xprice() * (xweight + moreweight);
                    }
                }
            }


            responseContainer.setExtramsg(String.valueOf(expressfee));

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

    @RequestMapping({"/caculateExpressFee"})
    @ResponseBody
    ResponseContainer caculateExpressFee(String session, int goodsid, int provinceid) {

        ResponseContainer responseContainer = new ResponseContainer();
        WeixinExpreeFeeMessage expreeFeeMessage = new WeixinExpreeFeeMessage();


        try {
            int expressfee = 0;
            int prefee = 0;

            Goods goods = goodsService.getById(goodsid);

            if (goods != null) {
                int feeid = goods.getFeeid();
                if (feeid == 0) {

                } else {
                    ShopExpressFee shopExpressFee = shopExpressFeeService.getById(feeid);
                    if (shopExpressFee != null) {
                        String provinces = shopExpressFee.getProvince_id_array();
                        if (!provinces.isEmpty()) {

                            int weight = goods.getWeight();
                            if (weight <= shopExpressFee.getWeight_snum()) {
                                prefee = shopExpressFee.getWeight_sprice();
                            } else {
                                int xweight = (weight - shopExpressFee.getWeight_snum()) / shopExpressFee.getWeight_xnum();
                                int moreweight = (weight - shopExpressFee.getWeight_snum()) % shopExpressFee.getWeight_xnum() == 0 ? 0 : 1;
                                prefee = shopExpressFee.getWeight_sprice() +
                                        shopExpressFee.getWeight_xprice() * (xweight + moreweight);
                            }
                            String[] provincearr = provinces.split(",");

                            for (String province : provincearr) {
                                int provincei = Integer.valueOf(province);
                                if (provincei == provinceid) {
                                    expressfee = prefee;

                                    break;
                                }
                            }
                        }


                    }
                }
                expreeFeeMessage.setExpressfee(expressfee);
                expreeFeeMessage.setPrefee(prefee);
                responseContainer.setMessage(expreeFeeMessage);
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


    @RequestMapping({"/getcategorys"})
    @ResponseBody
    ResponseContainer getcategorys(String session) {
        ResponseContainer responseContainer = new ResponseContainer();
        try {


            String categorys = (String) redisUtils.get(RedisKeyConstant.VALUE_GET_CATEGORYS);

            if (categorys != null) {
                Type type = new TypeToken<List<GoodsCategory>>() {
                }.getType();
                Gson gson = new Gson();

                List<GoodsCategory> categories = gson.fromJson(categorys, type);

                responseContainer.setLists(categories);
            } else {
                List<GoodsCategory> goodsCategories = goodsCategoryService.getAll();
                Gson gson = new Gson();
                String jsonstr = gson.toJson(goodsCategories);
                redisUtils.set(RedisKeyConstant.VALUE_GET_CATEGORYS, jsonstr);
                responseContainer.setLists(goodsCategories);
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


    @RequestMapping({"/getshipaddrs"})
    @ResponseBody
    ResponseContainer getshipaddrs(String session) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();
                List<CustomerShipAddr> customerShipAddrs = customerShipAddrService.GetByUserid(userid);

                responseContainer.setLists(customerShipAddrs);

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


    @RequestMapping({"/getuserinfo"})
    @ResponseBody
    ResponseContainer getuserinfo(String session, HttpServletRequest request) {


        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                responseContainer.setMessage(customer);
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




    @RequestMapping({"/setUserPersonal"})
    @ResponseBody
    ResponseContainer setUserPersonal(String session, String nickname, String headpic) {


        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                customer.setHeadpic(headpic);
                if (nickname != null && !"".equals(nickname)) {
                    nickname = nickname.replaceAll("[\ud800\udc00-\udbff\udfff\ud800-\udfff]", "");
                    logger.warn("nickname:" + nickname);
                }

                customer.setNickname(nickname);
                customer.setUpdatetime(new Date());
                customerService.update(customer);
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


    @RequestMapping({"/getdefaultaddr"})
    @ResponseBody
    ResponseContainer getdefaultaddr(String session) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();
                List<CustomerShipAddr> customerShipAddrs = customerShipAddrService.GetUserDefault(userid);

                if (customerShipAddrs != null && !customerShipAddrs.isEmpty()) {
                    responseContainer.setMessage(customerShipAddrs.get(0));
                } else {
                    responseContainer.setMessage(null);
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


    @RequestMapping({"/addweixinshipaddr"})
    @ResponseBody
    ResponseContainer addweixinshipaddr(String session, CustomerShipAddr shipAddr) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                shipAddr.setUid(userid);

                customerShipAddrService.update(shipAddr);

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


    @RequestMapping({"/transweixinaddr"})
    @ResponseBody
    ResponseContainer transweixinaddr(String session, CustomerShipAddr shipAddr) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            Customer customer = customerService.GetByToken(session);
            if (customer != null) {

                Province province = regionService.findProviceByname(shipAddr.getProvincename());
                City city = regionService.findCityByname(shipAddr.getCityname());
                List<District>  districts = regionService.findDistrictByname(shipAddr.getDistrictname());


                int cid = 0;
                int pid = 0;
                int did = 0;

                if (province != null && city != null && districts != null && !districts.isEmpty()) {
                    cid = city.getId();
                    pid = province.getId();

                    if(districts.size() == 1)
                    {
                        did = districts.get(0).getId();
                    }
                    else
                    {
                        for(District district:districts)
                        {
                            if(district.getP() == city.getId())
                            {
                                did = district.getId();
                                break;
                            }
                        }
                    }


                } else {
                    if (province == null) {
                        province = new Province();
                        province.setP(0);
                        province.setN(shipAddr.getProvincename());

                        regionService.addProvince(province);
                        pid = province.getId();
                    } else {
                        pid = province.getId();
                    }

                    if (city == null) {
                        city = new City();
                        city.setP(province.getId());
                        city.setN(shipAddr.getCityname());
                        regionService.addCity(city);

                        cid = city.getId();
                    } else {
                        cid = city.getId();
                    }

                    if (districts == null || districts.isEmpty()) {
                        District district = new District();
                        district.setP(city.getId());
                        district.setN(shipAddr.getDistrictname());
                        regionService.addDistrict(district);

                        did = district.getId();
                    } else {
                        if(districts.size() == 1)
                        {
                            did = districts.get(0).getId();
                        }
                        else
                        {
                            for(District district:districts)
                            {
                                if(district.getP() == city.getId())
                                {
                                    did = district.getId();
                                    break;
                                }
                            }
                        }
                    }

                }

                int userid = customer.getId();

                int count = customerShipAddrService.CountByUserid(userid);

                if (count == 0) {
                    shipAddr.setIsdefault(1);
                }


                shipAddr.setUid(userid);

                shipAddr.setCity(cid);
                shipAddr.setDistrict(did);
                shipAddr.setProvince(pid);

                customerShipAddrService.update(shipAddr);
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


    @RequestMapping({"/addshipaddr"})
    @ResponseBody
    ResponseContainer addshipaddr(String session, CustomerShipAddr shipAddr) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);
            if (customer != null) {
                int userid = customer.getId();

                int count = customerShipAddrService.CountByUserid(userid);

                if (count == 0) {
                    shipAddr.setIsdefault(1);
                }

                shipAddr.setUid(userid);

                customerShipAddrService.update(shipAddr);

                responseContainer.setMessage(shipAddr);

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

    @RequestMapping({"/getshipaddr"})
    @ResponseBody
    ResponseContainer getshipaddr(String session, int address_id) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);


            if (customer != null) {
                int userid = customer.getId();

                CustomerShipAddr customerShipAddr = customerShipAddrService.GetByid(address_id);

                if (customerShipAddr.getUid() == userid) {
                    responseContainer.setMessage(customerShipAddr);
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


    @RequestMapping({"/getsystemnotice"})
    @ResponseBody
    ResponseContainer getsystemnotice(String session, int shopid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            SystemNotice systemNotice = systemNoticeRepository.findByShopid(shopid);

            responseContainer.setMessage(systemNotice);

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


    @RequestMapping({"/getshopdetail"})
    @ResponseBody
    ResponseContainer getshopdetail(String session, int shopid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            String shopsdetails = (String) redisUtils.hmGet(RedisKeyConstant.VALUE_GET_SHOP_DETAILS, String.valueOf(shopid));

            if (shopsdetails != null) {
                Type type = new TypeToken<Shop>() {
                }.getType();
                Gson gson = new Gson();

                Shop shop = gson.fromJson(shopsdetails, type);

                responseContainer.setMessage(shop);

            } else {
                Shop shop = shopService.GetById(shopid);
                if (shop != null) {


                    Gson gson = new Gson();
                    String jsonstr = gson.toJson(shop);
                    redisUtils.hmSet(RedisKeyConstant.VALUE_GET_SHOP_DETAILS, String.valueOf(shopid), jsonstr);
                    responseContainer.setMessage(shop);

                }
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


    @RequestMapping({"/getshopgoods"})
    @ResponseBody
    ResponseContainer getshopgoods(String session, int shopid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {


            String goodslist = (String) redisUtils.hmGet(RedisKeyConstant.VALUE_GET_SHOP_GOODS, String.valueOf(shopid));

            if (goodslist != null) {
                Type type = new TypeToken<List<GoodsCoverInfo>>() {
                }.getType();
                Gson gson = new Gson();

                List<GoodsCoverInfo> goods = gson.fromJson(goodslist, type);

                responseContainer.setLists(goods);
            } else {
                List<GoodsBasic> goods = null;

                goods = goodsService.GetAllByShopid(shopid);

                List<GoodsCoverInfo> goodsCoverInfos = new ArrayList<>(goods.size());
                for (GoodsBasic goodsBasic : goods) {
                    GoodsCoverInfo goodsCoverInfo = new GoodsCoverInfo();
                    goodsCoverInfo.setId(goodsBasic.getId());
                    if (goodsBasic.getPictureurl() != null) {
                        goodsCoverInfo.setCover(goodsBasic.getPictureurl());
                    } else {
                        goodsCoverInfo.setCover(null);
                    }

                    goodsCoverInfo.setGroup_number(goodsBasic.getGroup_number());
                    goodsCoverInfo.setIntroduction(goodsBasic.getIntroduction());
                    goodsCoverInfo.setGroup_price(goodsBasic.getGroup_price());
                    goodsCoverInfos.add(goodsCoverInfo);
                }
                Gson gson = new Gson();
                String jsonstr = gson.toJson(goodsCoverInfos);
                redisUtils.hmSet(RedisKeyConstant.VALUE_GET_SHOP_GOODS, String.valueOf(shopid), jsonstr);
                responseContainer.setLists(goodsCoverInfos);

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

    @RequestMapping({"/editshipaddr"})
    @ResponseBody
    ResponseContainer editshipaddr(String session, CustomerShipAddr shipAddr) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);


            if (customer != null) {
                int userid = customer.getId();

                if (shipAddr.getUid() == userid) {
                    customerShipAddrService.update(shipAddr);
                    responseContainer.setMessage(shipAddr);
                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
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


    @RequestMapping({"/deleteshipaddr"})
    @ResponseBody
    ResponseContainer deleteshipaddr(String session, int addrid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);


            if (customer != null) {
                int userid = customer.getId();

                CustomerShipAddr customerShipAddr = customerShipAddrService.GetByid(addrid);
                if (customerShipAddr.getUid() == userid) {
                    boolean bautosetdefault = false;
                    if (customerShipAddr.getIsdefault() == CustomerShipAddr.ADDR_DEFAULT) {
                        bautosetdefault = true;
                    }
                    customerShipAddrService.delete(customerShipAddr);

                    if (bautosetdefault) {
                        List<CustomerShipAddr> customerShipAddrs = customerShipAddrService.GetByUserid(userid);
                        if (!customerShipAddrs.isEmpty()) {
                            customerShipAddrs.get(0).setIsdefault(CustomerShipAddr.ADDR_DEFAULT);
                            customerShipAddrService.update(customerShipAddrs.get(0));
                        }
                    }
                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
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





    @RequestMapping({"/onlogin"})
    @ResponseBody
    ResponseContainer onlogin(String code, int recommender) {
        ResponseContainer responseContainer = new ResponseContainer();

        StringBuilder sb = new StringBuilder("https://api.weixin.qq.com/sns/jscode2session?appid=");
        sb.append(WxPayConfig.appid);
        sb.append("&secret=");
        sb.append(WxPayConfig.APP_SECRET);
        sb.append("&js_code=");
        sb.append(code);
        sb.append("&grant_type=authorization_code");


        String url = sb.toString();

        try {
            ///tcpserver/reportstartresult
            String respone = HttpUtils.getInstance().sendHttpGet(url);

            if (respone != null) {

                JsonParser parser = new JsonParser();  //创建JSON解析器
                JsonObject object = (JsonObject) parser.parse(respone);

                if (object.has("openid")) {
                    String openid = object.get("openid").getAsString();
                    String sessionkey = object.get("session_key").getAsString();

                    Customer customer = customerService.GetByWeixin(openid);
                    Date date = new Date();

                    String dateStr = new SimpleDateFormat(AppCommon.COMMON_DATE_FORMAT).format(date);

                    String md5Str = Md5Util.getMD5(openid + sessionkey + dateStr);

                    if (customer != null) {

                        if (customer.getToken() != null) {
                            //redisTemplate.delete(customer.getToken());
                            customer.setToken(md5Str);
                            customerService.create(customer);
                        } else {
                            customer.setToken(md5Str);
                            customerService.create(customer);
                        }
                    } else {
                        customer = new Customer();
                        customer.setWeixin(openid);
                        customer.setSessionkey(sessionkey);
                        customer.setToken(md5Str);
                        customer.setBalance(0.0);
                        customer.setRecommender(recommender);
                        customer.setUpdatetime(new Date());
                        customerService.create(customer);

                        if(recommender>0)
                        {
                            Customer customerrecommender = customerService.GetById(recommender);

                            if(customerrecommender!=null)
                            {
                                customerrecommender.setRecommendcount(customerrecommender.getRecommendcount() + 1);
                                customerService.create(customerrecommender);
                            }
                        }



                    }
                    String idkey = String.valueOf(customer.getId());

                    WeixinAuthInfoMessage weixinAuthInfoMessage = new WeixinAuthInfoMessage();

                    weixinAuthInfoMessage.setSession(md5Str);
                    responseContainer.setMessage(weixinAuthInfoMessage);


                } else if (object.has("errcode")) {
                    int errcode = object.get("errcode").getAsInt();
                    String errmsg = object.get("errmsg").getAsString();

                    responseContainer.setReturncode(errcode);
                    responseContainer.setErrormsg(errmsg);
                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.WEIXIN_ONLOGIN_RETRUN_FAIL);
                    responseContainer.setErrormsg(ResponeCodeConstant.WEIXIN_ONLOGIN_RETRUN_FAIL_STR);
                }


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
