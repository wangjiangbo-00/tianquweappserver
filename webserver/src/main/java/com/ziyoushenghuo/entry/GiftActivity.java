package com.ziyoushenghuo.entry;

import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "zytc_promotion_gift")
public class GiftActivity extends BasicMessage {

    public static final int GIFT_STATUS_VOID = 0;
    public static final int GIFT_STATUS_APPLY = 1;
    public static final int GIFT_STATUS_APPLY_OK = 2;
    public static final int GIFT_STATUS_APPLY_FAIL = 3;
    public static final int GIFT_STATUS_CAN_SHOW = 4;
    public static final int GIFT_STATUS_START = 5;
    public static final int GIFT_STATUS_END = 6; //等待收礼者填写
    public static final int GIFT_STATUS_CLOSE = 7;


    public static final int GIFT_MODE_TIME_DUE = 1;
    public static final int GIFT_MODE_RANDOM = 2;
    public static final int GIFT_MODE_ASSIST = 3; //助力抽奖
    public static final int GIFT_MODE_PEOPLE_COUNT = 4; //满人开奖
    public static final int GIFT_MODE_OWNER_OPEN = 5; //手动开启


    public static final int GIFT_FROM_SHOP = 0;
    public static final int GIFT_FROM_USER = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gift_id", nullable = false)
    private int id;

    @Column(name = "start_time")
    private Date start_time;

    @Column(name = "end_time")
    private Date endtime;

    @Column(name = "days")
    private int days;

    @Column(name = "max_num")
    private int num;


    @Column(name = "ownerid")
    private int ownerid; //from =0 shopid from=1 userid




    @Column(name = "gift_name")
    private String gift_name;


    @Column(name = "create_time")
    private Date createtime;


    @Column(name = "modify_time")
    private Date modifytime;


    @Column(name = "status")
    private int status;


    @OneToOne
    @JoinColumn(name="goods_id")
    private  GoodsCover goodsCover;

    @Column(name = "goods_name")
    private String goods_name;


    @Column(name = "goods_picture")
    private String goods_picture;

    @Column(name = "ownername")
    private String ownername;


    @Column(name = "ownerpic")
    private String ownerpic;

    @Column(name = "remarks")
    private String remarks;


    @Column(name = "giftfrom")
    private int giftfrom;
    @Column(name = "mode")
    private int mode;

    @Column(name = "result")
    private String result;

    @Column(name = "personas")
    private String personas;


    @Column(name = "parms")
    private String parms;


    @Column(name = "participate")
    private int participate;

    @Column(name = "count")
    private int count;




    @Column(name = "appointment")
    private int appointment;


    @Column(name = "user_propagate")
    private String user_propagate;


    @Column(name = "qrpic")
    private String qrpic;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }



    public String getGift_name() {
        return gift_name;
    }

    public void setGift_name(String gift_name) {
        this.gift_name = gift_name;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public GoodsCover getGoodsCover() {
        return goodsCover;
    }

    public void setGoodsCover(GoodsCover goodsCover) {
        this.goodsCover = goodsCover;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getGoods_picture() {
        return goods_picture;
    }

    public void setGoods_picture(String goods_picture) {
        this.goods_picture = goods_picture;
    }



    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getParms() {
        return parms;
    }

    public void setParms(String parms) {
        this.parms = parms;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getParticipate() {
        return participate;
    }

    public void setParticipate(int participate) {
        this.participate = participate;
    }


    public int getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(int ownerid) {
        this.ownerid = ownerid;
    }

    public String getOwnername() {
        return ownername;
    }

    public void setOwnername(String ownername) {
        this.ownername = ownername;
    }

    public String getOwnerpic() {
        return ownerpic;
    }

    public void setOwnerpic(String ownerpic) {
        this.ownerpic = ownerpic;
    }

    public String getPersonas() {
        return personas;
    }

    public void setPersonas(String personas) {
        this.personas = personas;
    }

    public int getAppointment() {
        return appointment;
    }

    public void setAppointment(int appointment) {
        this.appointment = appointment;
    }


    public static int getGiftStatusVoid() {
        return GIFT_STATUS_VOID;
    }

    public int getGiftfrom() {
        return giftfrom;
    }

    public void setGiftfrom(int giftfrom) {
        this.giftfrom = giftfrom;
    }

    public String getUser_propagate() {
        return user_propagate;
    }

    public void setUser_propagate(String user_propagate) {
        this.user_propagate = user_propagate;
    }


    public String getQrpic() {
        return qrpic;
    }

    public void setQrpic(String qrpic) {
        this.qrpic = qrpic;
    }
}
