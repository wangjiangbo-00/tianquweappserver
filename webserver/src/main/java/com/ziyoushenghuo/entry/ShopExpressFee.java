package com.ziyoushenghuo.entry;





import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "zytc_order_shipping_fee")
public class ShopExpressFee extends BasicMessage {

    public static final int FREE_MODE_FREE = 1;
    public static final int FREE_MODE_WEIGHT = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipping_fee_id", nullable = false)
    private int id;

    @Column(name = "shop_id",nullable = false)
    private int shopid;

    @Column(name = "province_id_array",nullable = false)
    private String province_id_array;


    @Column(name = "fee_mode",nullable = false)
    private int fee_mode;


    @Column(name = "weight_snum",nullable = false)
    private int weight_snum;

    @Column(name = "weight_sprice",nullable = false)
    private int weight_sprice;

    @Column(name = "weight_xnum",nullable = false)
    private int weight_xnum;

    @Column(name = "weight_xprice",nullable = false)
    private int weight_xprice;

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

    public String getProvince_id_array() {
        return province_id_array;
    }

    public void setProvince_id_array(String province_id_array) {
        this.province_id_array = province_id_array;
    }

    public int getFee_mode() {
        return fee_mode;
    }

    public void setFee_mode(int fee_mode) {
        this.fee_mode = fee_mode;
    }

    public static int getFreeModeFree() {
        return FREE_MODE_FREE;
    }

    public static int getFreeModeWeight() {
        return FREE_MODE_WEIGHT;
    }

    public int getWeight_snum() {
        return weight_snum;
    }

    public void setWeight_snum(int weight_snum) {
        this.weight_snum = weight_snum;
    }

    public int getWeight_sprice() {
        return weight_sprice;
    }

    public void setWeight_sprice(int weight_sprice) {
        this.weight_sprice = weight_sprice;
    }

    public int getWeight_xnum() {
        return weight_xnum;
    }

    public void setWeight_xnum(int weight_xnum) {
        this.weight_xnum = weight_xnum;
    }

    public int getWeight_xprice() {
        return weight_xprice;
    }

    public void setWeight_xprice(int weight_xprice) {
        this.weight_xprice = weight_xprice;
    }
}
