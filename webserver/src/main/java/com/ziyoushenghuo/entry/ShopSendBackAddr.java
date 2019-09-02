package com.ziyoushenghuo.entry;





import com.fasterxml.jackson.annotation.JsonInclude;
import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "zytc_order_shop_return")
public class ShopSendBackAddr extends BasicMessage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id", nullable = false)
    private int shopid;

    @Column(name = "shop_address",nullable = true)
    private String shop_address;

    @Column(name = "seller_name",nullable = true)
    private String seller_name;

    @Column(name = "seller_mobile",nullable = true)
    private String seller_mobile;

    @Column(name = "seller_zipcode",nullable = true)
    private String seller_zipcode;


    public int getShopid() {
        return shopid;
    }

    public void setShopid(int shopid) {
        this.shopid = shopid;
    }

    public String getShop_address() {
        return shop_address;
    }

    public void setShop_address(String shop_address) {
        this.shop_address = shop_address;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public String getSeller_mobile() {
        return seller_mobile;
    }

    public void setSeller_mobile(String seller_mobile) {
        this.seller_mobile = seller_mobile;
    }

    public String getSeller_zipcode() {
        return seller_zipcode;
    }

    public void setSeller_zipcode(String seller_zipcode) {
        this.seller_zipcode = seller_zipcode;
    }
}
