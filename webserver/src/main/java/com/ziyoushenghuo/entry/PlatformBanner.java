package com.ziyoushenghuo.entry;

import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "platform_banner")
public class PlatformBanner extends BasicMessage{


    public static final int PLATFROMBANNER_TYPE_VOID = 0;
    public static final int PLATFROMBANNER_TYPE_GOODS = 1;
    public static final int PLATFROMBANNER_TYPE_SHOP = 2;
    public static final int PLATFROMBANNER_TYPE_DISCOUNT = 3;
    public static final int PLATFROMBANNER_TYPE_GIFT = 4;
    public static final int PLATFROMBANNER_TYPE_GROUP = 5;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "type")
    private int type;

    @Column(name = "refid")
    private int refid;

    @Column(name = "poster")
    private String poster;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "is_show")
    private int isshow;

    @Column(name = "create_time")
    private Date createtime;

    @Column(name = "modify_time")
    private Date modifytime;

    @Column(name = "sort")
    private int sort;

    @Column(name = "name")
    private String name;

    @Column(name = "is_valid")
    private int isvalid;

    @Column(name = "refdetails")
    private String refdetails;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRefid() {
        return refid;
    }

    public void setRefid(int refid) {
        this.refid = refid;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsshow() {
        return isshow;
    }

    public void setIsshow(int isshow) {
        this.isshow = isshow;
    }

    public int getIsvalid() {
        return isvalid;
    }

    public void setIsvalid(int isvalid) {
        this.isvalid = isvalid;
    }

    public String getRefdetails() {
        return refdetails;
    }

    public void setRefdetails(String refdetails) {
        this.refdetails = refdetails;
    }
}
