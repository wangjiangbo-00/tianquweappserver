package com.ziyoushenghuo.entry;





import com.fasterxml.jackson.annotation.JsonInclude;
import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "zytc_shop")
public class ShopCover extends BasicMessage  {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id", nullable = false)
    private int id;

    @Column(name = "shop_logo",nullable = true)
    private String shoplogo;


    @Column(name = "shop_name",nullable = true)
    private String shopname;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShoplogo() {
        return shoplogo;
    }

    public void setShoplogo(String shoplogo) {
        this.shoplogo = shoplogo;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }
}
