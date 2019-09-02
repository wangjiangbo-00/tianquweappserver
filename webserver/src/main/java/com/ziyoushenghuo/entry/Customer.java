package com.ziyoushenghuo.entry;

import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "zytc_customers")
public class Customer extends BasicMessage{

    public static final int ACTION_SET = 1;
    public static final int ACTION_UNSET = 2;

    public static final int RECOMME_ORDER_COUNT = 10;
    public static final double RECOMME_ORDER_AMOUNT = 680;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "weixin",nullable = true)
    private String weixin;

    @Column(name = "phone",nullable = true)
    private String phone;

    @Column(name = "sessionkey",nullable = true)
    private String sessionkey;

    @Column(name = "balance",nullable = true)
    private Double balance;

    @Column(name = "headpic",nullable = true)
    private String headpic;

    @Column(name = "nickname",nullable = true)
    private String nickname;

    @Column(name = "token",nullable = true)
    private String token;

    @Column(name = "updatetime")
    private Date updatetime;

    @Column(name = "shopcollect",nullable = true)
    private String shopcollect;

    @Column(name = "goodscollect",nullable = true)
    private String goodscollect;


    @Column(name = "articlecollect",nullable = true)
    private String articlecollect;

    @Column(name = "recommender",nullable = true)
    private int recommender;

    @Column(name = "configflag",nullable = true)
    private int configflag;

    @Column(name = "isrecommender",nullable = true)
    private int isrecommender;


    @Column(name = "recommendcount",nullable = true)
    private int recommendcount;


    @Column(name = "recommendertime",nullable = true)
    private Date recommendertime;


    @Column(name = "qrpic",nullable = true)
    private String qrpic;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSessionkey() {
        return sessionkey;
    }

    public void setSessionkey(String sessionkey) {
        this.sessionkey = sessionkey;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public String getShopcollect() {
        return shopcollect;
    }

    public void setShopcollect(String shopcollect) {
        this.shopcollect = shopcollect;
    }

    public String getGoodscollect() {
        return goodscollect;
    }

    public void setGoodscollect(String goodscollect) {
        this.goodscollect = goodscollect;
    }

    public int getRecommender() {
        return recommender;
    }

    public void setRecommender(int recommender) {
        this.recommender = recommender;
    }

    public int getRecommendcount() {
        return recommendcount;
    }

    public void setRecommendcount(int recommendcount) {
        this.recommendcount = recommendcount;
    }

    public Date getRecommendertime() {
        return recommendertime;
    }

    public void setRecommendertime(Date recommendertime) {
        this.recommendertime = recommendertime;
    }

    public int getIsrecommender() {
        return isrecommender;
    }

    public void setIsrecommender(int isrecommender) {
        this.isrecommender = isrecommender;
    }

    public String getArticlecollect() {
        return articlecollect;
    }

    public void setArticlecollect(String articlecollect) {
        this.articlecollect = articlecollect;
    }

    public String getQrpic() {
        return qrpic;
    }

    public void setQrpic(String qrpic) {
        this.qrpic = qrpic;
    }

    public Customer(){}
    public Customer(int id,String nickname)
    {
        this.id = id;
        this.nickname = nickname;
    }


    public Customer(int id ,int isrecommender,int recommender)
    {
        this.id = id;
        this.isrecommender = isrecommender;

        this.recommender = recommender;
    }

    public Customer(String weixin)
    {

        this.weixin = weixin;
    }

    public Customer(int id,String nickname,String pic)
    {
        this.id = id;
        this.nickname = nickname;
        this.headpic = pic;
    }


    public Customer(int id,String nickname,String pic,Date date)
    {
        this.id = id;
        this.nickname = nickname;
        this.headpic = pic;
        this.updatetime = date;
    }

    public Customer(int id,String nickname,String pic,String weixin)
    {
        this.id = id;
        this.nickname = nickname;
        this.headpic = pic;
        this.weixin = weixin;
    }

    public Customer(int id,int configflag)
    {
        this.id = id;
        this.configflag = configflag;

    }


    public int getConfigflag() {
        return configflag;
    }

    public void setConfigflag(int configflag) {
        this.configflag = configflag;
    }
}
