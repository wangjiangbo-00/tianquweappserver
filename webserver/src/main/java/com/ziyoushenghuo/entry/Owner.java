package com.ziyoushenghuo.entry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "sys_user")
public class Owner extends BasicMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", nullable = false)
    private int id;


    @Column(name = "user_name",nullable = false)
    private String username;

    @JsonIgnore
    @Column(name = "user_password",nullable = false)
    private String password;


    @JsonIgnore
    @Column(name = "securitypwd",nullable = false)
    private String securitypwd;

    @JsonIgnore
    @Column(name = "wx_openid",nullable = true)
    private String weixin;

    @Column(name = "user_tel",nullable = true)
    private String phone;


    @Column(name = "last_login_time",nullable = true)
    private Date last_login_time;

    @Column(name = "last_login_ip",nullable = true)
    private String last_ip;

    @Column(name = "real_name",nullable = true)
    private String nikename;

    @Column(name = "user_headimg",nullable = true)
    private String headpic;

    @Column(name = "token",nullable = true)
    private String token;

    @Column(name = "instance_id",nullable = true)
    private int shopid;


    @Column(name = "is_member",nullable = true)
    private int ismember;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecuritypwd() {
        return securitypwd;
    }

    public void setSecuritypwd(String securitypwd) {
        this.securitypwd = securitypwd;
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

    public Date getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(Date last_login_time) {
        this.last_login_time = last_login_time;
    }

    public String getLast_ip() {
        return last_ip;
    }

    public void setLast_ip(String last_ip) {
        this.last_ip = last_ip;
    }

    public String getNikename() {
        return nikename;
    }

    public void setNikename(String nikename) {
        this.nikename = nikename;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getShopid() {
        return shopid;
    }

    public void setShopid(int shopid) {
        this.shopid = shopid;
    }


    public int getIsmember() {
        return ismember;
    }

    public void setIsmember(int ismember) {
        this.ismember = ismember;
    }
}
