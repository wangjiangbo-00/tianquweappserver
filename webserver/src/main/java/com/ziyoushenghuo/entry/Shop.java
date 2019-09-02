package com.ziyoushenghuo.entry;





import com.fasterxml.jackson.annotation.JsonInclude;
import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "zytc_shop")
public class Shop extends BasicMessage  {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id", nullable = false)
    private int id;

    @Column(name = "uid",nullable = false)
    private int ownerid;


    @Column(name = "province_id",nullable = true)
    private int provinceid;

    @Column(name = "city_id",nullable = true)
    private int cityid;

    @Column(name = "provincename",nullable = true)
    private String provincename;

    @Column(name = "cityname",nullable = true)
    private String cityname;

    @Column(name = "shop_name",nullable = true)
    private String shopname;

    @Column(name = "shop_company_name",nullable = true)
    private String shop_company_name;

    @Column(name = "shopcontact",nullable = true)
    private String shopcontact;


    @Column(name = "shop_state",nullable = true)
    private int shopstate;


    @Column(name = "shop_collect",nullable = true)
    private int shop_collect;


    @Column(name = "shopsales",nullable = true)
    private int shopsales;

    @Column(name = "shop_close_info",nullable = true)
    private String shop_close_nfo;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Column(name = "shop_create_time",nullable = true)
    private Date shopcreatetime;


    @Column(name = "shop_banner",nullable = true)
    private String shopbanner;

    @Column(name = "shop_logo",nullable = true)
    private String shoplogo;


    @Column(name = "shop_platform_commission_rate",nullable = true)
    private int shop_platform_commission_rate;

    @Column(name = "shop_weixinpay_rate",nullable = true)
    private int shop_weixinpay_rate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Column(name = "shoptitle",nullable = true)
    private String shoptitle;


    @Column(name = "shop_description",nullable = true)
    private String shopdesc;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(int ownerid) {
        this.ownerid = ownerid;
    }

    public int getProvinceid() {
        return provinceid;
    }

    public void setProvinceid(int provinceid) {
        this.provinceid = provinceid;
    }

    public int getCityid() {
        return cityid;
    }

    public void setCityid(int cityid) {
        this.cityid = cityid;
    }



    public int getShopstate() {
        return shopstate;
    }

    public void setShopstate(int shopstate) {
        this.shopstate = shopstate;
    }

    public String getShop_close_nfo() {
        return shop_close_nfo;
    }

    public void setShop_close_nfo(String shop_close_nfo) {
        this.shop_close_nfo = shop_close_nfo;
    }

    public Date getShopcreatetime() {
        return shopcreatetime;
    }

    public void setShopcreatetime(Date shopcreatetime) {
        this.shopcreatetime = shopcreatetime;
    }

    public String getShopbanner() {
        return shopbanner;
    }

    public void setShopbanner(String shopbanner) {
        this.shopbanner = shopbanner;
    }

    public String getShoplogo() {
        return shoplogo;
    }

    public void setShoplogo(String shoplogo) {
        this.shoplogo = shoplogo;
    }



    public int getShop_platform_commission_rate() {
        return shop_platform_commission_rate;
    }

    public void setShop_platform_commission_rate(int shop_platform_commission_rate) {
        this.shop_platform_commission_rate = shop_platform_commission_rate;
    }

    public int getShop_weixinpay_rate() {
        return shop_weixinpay_rate;
    }

    public void setShop_weixinpay_rate(int shop_weixinpay_rate) {
        this.shop_weixinpay_rate = shop_weixinpay_rate;
    }

    public String getShoptitle() {
        return shoptitle;
    }

    public void setShoptitle(String shoptitle) {
        this.shoptitle = shoptitle;
    }

    public String getShopdesc() {
        return shopdesc;
    }

    public void setShopdesc(String shopdesc) {
        this.shopdesc = shopdesc;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getShop_company_name() {
        return shop_company_name;
    }

    public void setShop_company_name(String shop_company_name) {
        this.shop_company_name = shop_company_name;
    }


    public String getProvincename() {
        return provincename;
    }

    public void setProvincename(String provincename) {
        this.provincename = provincename;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }


    public int getShop_collect() {
        return shop_collect;
    }

    public void setShop_collect(int shop_collect) {
        this.shop_collect = shop_collect;
    }


    public String getShopcontact() {
        return shopcontact;
    }

    public void setShopcontact(String shopcontact) {
        this.shopcontact = shopcontact;
    }


    public int getShopsales() {
        return shopsales;
    }

    public void setShopsales(int shopsales) {
        this.shopsales = shopsales;
    }
}
