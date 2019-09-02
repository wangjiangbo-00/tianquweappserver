package com.ziyoushenghuo.web.controller;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.entry.*;
import com.ziyoushenghuo.rabbitmq.MQConstant;

import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.repository.QuestionRepositor;
import com.ziyoushenghuo.response.*;
import com.ziyoushenghuo.service.*;

import com.ziyoushenghuo.weixinpay.WxPayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by RXJ on 2017/4/7.
 */
@Controller
@RequestMapping({"/ziyoutechan/system"})
public class SystemConfigController {

    @Autowired
    private RegionService regionService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private RedisTemplate redisTemplate;

    private static Logger logger = LoggerFactory.getLogger(SystemConfigController.class);

    @RequestMapping({"/getallregions"})
    @ResponseBody
    ResponseContainer getallregions() {

        ResponseContainer responseContainer = new ResponseContainer();


        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();

            String key = RedisKeyConstant.VALUE_SYS_REGIONS;


            String regionstr = operations.get(key);

            if (regionstr == null) {
                List<Province> provinces = regionService.GetAllProvince();
                List<City> cities = regionService.GetAllCity();
                List<District> districts = regionService.GetAllDistrict();

                WeixinRegions weixinRegions = new WeixinRegions();
                weixinRegions.setCityList(cities);
                weixinRegions.setDistrictList(districts);
                weixinRegions.setProvinceList(provinces);

                Gson gson = new Gson();
                String json = gson.toJson(weixinRegions);
                operations.set(key, json);
                responseContainer.setMessage(weixinRegions);
            } else {
                Type type = new TypeToken<WeixinRegions>() {
                }.getType();
                Gson gson = new Gson();

                WeixinRegions weixinRegions = gson.fromJson(regionstr, type);
                responseContainer.setMessage(weixinRegions);
            }
        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
            responseContainer.setErrormsg(e.getMessage().substring(0, len));
            logger.warn(e.getMessage().substring(0, len));
        }


        return responseContainer;

    }


    @RequestMapping({"/getallquestions"})
    @ResponseBody
    ResponseContainer getallquestions() {

        ResponseContainer responseContainer = new ResponseContainer();


        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();

            String key = RedisKeyConstant.VALUE_SYS_QUESTIONS;


            String regionstr = operations.get(key);

            if (regionstr == null) {
                List<Question> questions = questionService.getAll();

                HashMap<Integer, WeixinShopQuestions> weixinShopQuestionsHashMap = new HashMap<Integer, WeixinShopQuestions>(16);

                for (Question question : questions) {
                    if (!weixinShopQuestionsHashMap.containsKey(question.getCatid())) {
                        WeixinShopQuestions weixinShopQuestions = new WeixinShopQuestions();

                        QuestionCategory questionCategory = questionService.getQuestionCategoryById(question.getCatid());

                        if (questionCategory != null) {
                            weixinShopQuestions.setCatid(questionCategory.getId());
                            weixinShopQuestions.setCatname(questionCategory.getCatname());
                            weixinShopQuestions.setLogo(questionCategory.getLogo());

                            List<Question> questionList = new ArrayList<>();

                            questionList.add(question);

                            weixinShopQuestions.setQuestionList(questionList);

                            weixinShopQuestionsHashMap.put(question.getCatid(), weixinShopQuestions);
                        } else {
                            continue;
                        }


                    } else {
                        WeixinShopQuestions weixinShopQuestions = weixinShopQuestionsHashMap.get(question.getCatid());

                        weixinShopQuestions.getQuestionList().add(question);
                    }
                }


                List<WeixinShopQuestions> weixinShopQuestionsList = new ArrayList<>();

                Collection<WeixinShopQuestions> weixinShopQuestionsCollection = weixinShopQuestionsHashMap.values();

                Iterator<WeixinShopQuestions> it2 = weixinShopQuestionsCollection.iterator();
                while (it2.hasNext()) {
                    weixinShopQuestionsList.add(it2.next());
                }

                Gson gson = new Gson();
                String json = gson.toJson(weixinShopQuestionsList);
                operations.set(key, json);
                responseContainer.setLists(weixinShopQuestionsList);
            } else {

                Type type = new TypeToken<List<WeixinShopQuestions>>() {
                }.getType();
                Gson gson = new Gson();

                List<WeixinShopQuestions> weixinShopQuestionsList = gson.fromJson(regionstr, type);
                responseContainer.setLists(weixinShopQuestionsList);
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


    @RequestMapping({"/search"})
    @ResponseBody
    ResponseContainer search(String keyword) {

        ResponseContainer responseContainer = new ResponseContainer();


        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();

            String key = RedisKeyConstant.VALUE_SYS_SEARCH_GOODS;

            List<GoodsCoverInfo> goodsSearches = null;
            String regionstr = operations.get(key);

            if (regionstr == null) {

                List<GoodsBasic> goodsBasics = goodsService.getSearchGoods();

                goodsSearches = new ArrayList<>(goodsBasics.size());
                for (GoodsBasic goodsBasic : goodsBasics) {
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
                    goodsSearches.add(goodsCoverInfo);
                }


                Gson gson = new Gson();
                String json = gson.toJson(goodsSearches);
                operations.set(key, json);
            } else {
                Type type = new TypeToken<List<GoodsCoverInfo>>() {
                }.getType();
                Gson gson = new Gson();

                goodsSearches = gson.fromJson(regionstr, type);
            }

            List<GoodsCoverInfo> goodsSearchList = new ArrayList<GoodsCoverInfo>();
            for (GoodsCoverInfo goodsCoverInfo : goodsSearches) {
                if (goodsCoverInfo.getIntroduction().contains(keyword)) {
                    goodsSearchList.add(goodsCoverInfo);
                }
            }
            responseContainer.setLists(goodsSearchList);


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


    @RequestMapping({"/getshopprovinces"})
    @ResponseBody
    ResponseContainer getshopprovinces() {

        ResponseContainer responseContainer = new ResponseContainer();


        try {

            ValueOperations<String, String> operations = redisTemplate.opsForValue();

            String key = RedisKeyConstant.VALUE_SYS_SHOP_PROVINCES;


            String regionstr = operations.get(key);

            if (regionstr == null) {
                List<Shop> provinces = shopService.GetShopProvinces();

                HashMap<Integer, WeixinShopRegions> weixinShopRegionsHashMap = new HashMap<Integer, WeixinShopRegions>();

                for (Shop shop : provinces) {
                    if (!weixinShopRegionsHashMap.containsKey(shop.getProvinceid())) {
                        WeixinShopRegions weixinShopRegions = new WeixinShopRegions();
                        weixinShopRegions.setProvinceid(shop.getProvinceid());
                        weixinShopRegions.setProvincename(shop.getProvincename());
                        List<Shop> cities = new ArrayList<Shop>();
                        cities.add(shop);
                        weixinShopRegions.setCityList(cities);

                        weixinShopRegionsHashMap.put(shop.getProvinceid(), weixinShopRegions);
                    } else {
                        WeixinShopRegions weixinShopRegions = weixinShopRegionsHashMap.get(shop.getProvinceid());

                        weixinShopRegions.getCityList().add(shop);
                    }
                }

                List<WeixinShopRegions> weixinShopRegionsList = new ArrayList<WeixinShopRegions>();

                Collection<WeixinShopRegions> weixinShopRegionsCollection = weixinShopRegionsHashMap.values();

                Iterator<WeixinShopRegions> it2 = weixinShopRegionsCollection.iterator();
                while (it2.hasNext()) {
                    weixinShopRegionsList.add(it2.next());
                }

                Gson gson = new Gson();
                String json = gson.toJson(weixinShopRegionsList);
                operations.set(key, json);


                responseContainer.setLists(weixinShopRegionsList);

            } else {
                Type type = new TypeToken<List<WeixinShopRegions>>() {
                }.getType();
                Gson gson = new Gson();

                List<WeixinShopRegions> weixinShopRegionsList = gson.fromJson(regionstr, type);
                responseContainer.setLists(weixinShopRegionsList);
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

    @RequestMapping({"/getshopcitys"})
    @ResponseBody
    ResponseContainer getshopcitys(int provinceid) {

        ResponseContainer responseContainer = new ResponseContainer();


        try {

            List<Shop> provinces = shopService.GetShopCitys(provinceid);

            responseContainer.setLists(provinces);


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


    @RequestMapping({"/getRegionDataVerison"})
    @ResponseBody
    ResponseContainer getRegionDataVerison() {


        ResponseContainer responseContainer = new ResponseContainer();
        try {

            ValueOperations<String, String> operations = redisTemplate.opsForValue();

            String version = operations.get(RedisKeyConstant.VALUE_SYS_REGION_VERSION);

            int version_i = 0;

            if (version != null) {
                version_i = Integer.valueOf(version);
            } else {
                operations.set(RedisKeyConstant.VALUE_SYS_REGION_VERSION, String.valueOf(version_i));
            }
            responseContainer.setExtramsg("" + version_i);
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


    @RequestMapping({"/getQuestionVerison"})
    @ResponseBody
    ResponseContainer getQuestionVerison() {


        ResponseContainer responseContainer = new ResponseContainer();
        try {

            ValueOperations<String, String> operations = redisTemplate.opsForValue();

            String version = operations.get(RedisKeyConstant.VALUE_SYS_QUESTION_VERSION);

            int version_i = 0;

            if (version != null) {
                version_i = Integer.valueOf(version);
            } else {
                operations.set(RedisKeyConstant.VALUE_SYS_QUESTION_VERSION, String.valueOf(version_i));
            }
            responseContainer.setExtramsg("" + version_i);
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

    @RequestMapping({"/getallprovinces"})
    @ResponseBody
    ResponseContainer getallprovinces() {

        ResponseContainer responseContainer = new ResponseContainer();

        List<Province> regions = new ArrayList<Province>();

        try {

            regions = regionService.GetAllProvince();

            responseContainer.setLists(regions);
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


    @RequestMapping({"/getcitys"})
    @ResponseBody
    ResponseContainer getcitys(int provinceid) {

        ResponseContainer responseContainer = new ResponseContainer();

        List<City> regions = new ArrayList<City>();

        try {
            regions = regionService.GetAllProvinceCity(provinceid);

            responseContainer.setLists(regions);
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


    @RequestMapping({"/getdistricts"})
    @ResponseBody
    ResponseContainer getdistricts(int cityid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            List<District> regions = new ArrayList<District>();

            regions = regionService.GetAllCityDistricts(cityid);

            responseContainer.setLists(regions);
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


    @RequestMapping({"/getqiniutoken"})
    @ResponseBody
    ResponseContainer getqiniutoken() {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();

            String token = operations.get(RedisKeyConstant.TOKEN_ZXTC_QINIU_VALUE);


            responseContainer.setExtramsg("" + token);

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


    @RequestMapping({"/gettxosskey"})
    @ResponseBody
    ResponseContainer gettxosskey() {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();

            String token = operations.get(RedisKeyConstant.TOKEN_ZXTC_TXOSS_VALUE);


            responseContainer.setExtramsg("" + token);

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
