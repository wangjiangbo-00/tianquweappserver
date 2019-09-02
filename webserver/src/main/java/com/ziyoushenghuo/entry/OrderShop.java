package com.ziyoushenghuo.entry;





import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "zytc_order_shop")
public class OrderShop {

    @Id
    @Column(name = "order_id", nullable = false)
    private Long id;

    @Column(name = "shop_id")
    private int shopId;

    @Column(name = "shop_name")
    private String shop_name;

    @Column(name = "shopcontact")
    private String shopcontact;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getShopcontact() {
        return shopcontact;
    }

    public void setShopcontact(String shopcontact) {
        this.shopcontact = shopcontact;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }
}
