package com.ziyoushenghuo.entry;

import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "zytc_customer_recommender_activecode")
public class CustomerRecommendActivecode extends BasicMessage {


    public static final int CODE_STATUS_NORMAL = 0;
    public static final int CODE_STATUS_SENDOUT = 1;
    public static final int CODE_STATUS_USED = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "status", nullable = false)
    private int status;

    @Column(name = "createtime")
    private Date createtime;

    @Column(name = "code",nullable = false)
    private String code; // 销售额

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
