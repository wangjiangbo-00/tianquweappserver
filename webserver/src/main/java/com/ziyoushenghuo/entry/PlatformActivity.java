package com.ziyoushenghuo.entry;

import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "platform_activity")
public class PlatformActivity extends BasicMessage{


    public static final int PLATFROMACTIVITY_STATUS_VOID = 0;
    public static final int PLATFROMACTIVITY_STATUS_APPLY = 1;
    public static final int PLATFROMACTIVITY_STATUS_APPLY_OK = 2;
    public static final int PLATFROMACTIVITY_STATUS_APPLY_FAIL = 3;
    public static final int PLATFROMACTIVITY_STATUS_CAN_SHOW = 4;
    public static final int PLATFROMACTIVITY_STATUS_START = 5;
    public static final int PLATFROMACTIVITY_STATUS_END = 6; //等待收礼者填写
    public static final int PLATFROMACTIVITY_STATUS_CLOSE = 7;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "discount_id")
    private int discountid;

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

    @Column(name = "type")
    private int type;

    @Column(name = "is_visible")
    private int isvisible;


    @Column(name = "img")
    private String img;

    @Column(name = "desp")
    private String desp;

    @Column(name = "is_open")
    private int isopen;

    @Column(name = "sort")
    private int sort;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDiscountid() {
        return discountid;
    }

    public void setDiscountid(int discountid) {
        this.discountid = discountid;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIsvisible() {
        return isvisible;
    }

    public void setIsvisible(int isvisible) {
        this.isvisible = isvisible;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public int getIsopen() {
        return isopen;
    }

    public void setIsopen(int isopen) {
        this.isopen = isopen;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
