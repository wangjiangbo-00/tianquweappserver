package com.ziyoushenghuo.entry;

import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "zytc_promotion_discount")
public class PromotionDiscount extends BasicMessage{



    public static final int STATUS_VOID = 0;
    public static final int STATUS_APPLY = 1;
    public static final int STATUS_APPLY_OK = 2;
    public static final int STATUS_APPLY_FAIL = 3;
    public static final int STATUS_CAN_SHOW = 4;
    public static final int STATUS_START = 5;
    public static final int STATUS_END = 6; //等待收礼者填写
    public static final int STATUS_CLOSE = 7;


    public static final int RPOMOTION_TYPE_SHOP = 1;
    public static final int RPOMOTION_TYPE_LONG = 2;
    public static final int RPOMOTION_TYPE_LIMIT = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discount_id", nullable = false)
    private int id;

    @Column(name = "shop_id")
    private int shopid;


    @Column(name = "shop_name")
    private String shopname;

    @Column(name = "discount_name")
    private String discount_name;

    @Column(name = "start_time")
    private Date starttime;


    @Column(name = "end_time")
    private Date endtime;

    @Column(name = "status")
    private int status;



    @Column(name = "create_time")
    private Date createtime;

    @Column(name = "modify_time")
    private Date modifytime;


    @Column(name = "pid")
    private int pid;

    @Column(name = "type")
    private int type;


    @Column(name = "is_visible")
    private int isvisible;

    @Column(name = "level")
    private int level;

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

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getDiscount_name() {
        return discount_name;
    }

    public void setDiscount_name(String discount_name) {
        this.discount_name = discount_name;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getModifytime() {
        return modifytime;
    }

    public void setModifytime(Date modifytime) {
        this.modifytime = modifytime;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getIsvisible() {
        return isvisible;
    }

    public void setIsvisible(int isvisible) {
        this.isvisible = isvisible;
    }
}
