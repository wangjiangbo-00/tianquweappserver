package com.ziyoushenghuo.entry;





import com.fasterxml.jackson.annotation.JsonInclude;
import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "zytc_pickup_point")
public class ShopPickupPoint extends BasicMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "shop_id",nullable = false)
    private int shopid; // 销售额

    @Column(name = "name",nullable = false)
    private String name; //当前余额

    @Column(name = "address",nullable = false)
    private String address; //提现过程金额

    @Column(name = "contact",nullable = false)
    private String contact; //去除微信和平台扣费后的金额

    @Column(name = "phone",nullable = false)
    private String phone;

    @Column(name = "cityname",nullable = false)
    private String cityname; //提现过程金额

    @Column(name = "provincename",nullable = false)
    private String provincename; //去除微信和平台扣费后的金额

    @Column(name = "districtname",nullable = false)
    private String districtname;





    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getShopid() {
        return shopid;
    }

    public void setShopid(int shopid) {
        this.shopid = shopid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getProvincename() {
        return provincename;
    }

    public void setProvincename(String provincename) {
        this.provincename = provincename;
    }

    public String getDistrictname() {
        return districtname;
    }

    public void setDistrictname(String districtname) {
        this.districtname = districtname;
    }


}
