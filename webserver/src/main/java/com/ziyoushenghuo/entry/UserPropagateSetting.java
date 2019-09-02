package com.ziyoushenghuo.entry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;


@Entity
@Table(name = "zytc_user_propagate_setting")
public class UserPropagateSetting extends BasicMessage {

    public static final int SETTING_NOMAL = 0;
    public static final int SETTING_DEFAULT = 1;

    public static final int SETTING_TYPE_WEIXIN_PERSONAL = 0; //个人微信号
    public static final int SETTING_TYPE_WEIXIN_PUBLIC = 1;//公众号
    public static final int SETTING_TYPE_SHOP = 2;//线下店铺

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;


    @Column(name = "uid",nullable = false)
    private int uid;

    @Column(name = "type",nullable = false)
    private int type;


    @Column(name = "name")
    private String name;

    @Column(name = "introduction")
    private String introduction;

    @Column(name = "spreadvalue")
    private String spreadvalue;

    @Column(name = "is_default")
    private int isdefault;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getSpreadvalue() {
        return spreadvalue;
    }

    public void setSpreadvalue(String spreadvalue) {
        this.spreadvalue = spreadvalue;
    }

    public int getIsdefault() {
        return isdefault;
    }

    public void setIsdefault(int isdefault) {
        this.isdefault = isdefault;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
