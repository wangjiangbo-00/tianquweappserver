package com.ziyoushenghuo.entry;

import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "zytc_customer_recommendimg")
public class CustomerRecommendImg extends BasicMessage {


    public static final int CUSTOM_RECOMMEND_TYPE_LOTTERY = 1;
    public static final int CUSTOM_RECOMMEND_TYPE_LOTTERY_RELAY = 2;
    public static final int CUSTOM_RECOMMEND_TYPE_SUPERGROUP = 3;
    public static final int CUSTOM_RECOMMEND_TYPE_DISCOUNT= 4;
    public static final int CUSTOM_RECOMMEND_TYPE_GROUP = 5;
    public static final int CUSTOM_RECOMMEND_TYPE_FREEORDER = 6;


    public static final int CUSTOM_RECOMMEND_TYPE_USERR_RECOMMENDER = 7; // 不会出现在这里面，前端需要这个类型
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "cid", nullable = false)
    private int cid;

    @Column(name = "refid",nullable = true)
    private int refid; // 销售额

    @Column(name = "tid",nullable = false)
    private int tid; // 销售额

    @Column(name = "pic",nullable = false)
    private String pic; // 销售额

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getRefid() {
        return refid;
    }

    public void setRefid(int refid) {
        this.refid = refid;
    }
}
