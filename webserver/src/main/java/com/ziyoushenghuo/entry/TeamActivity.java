package com.ziyoushenghuo.entry;





import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "zytc_team_activity")
public class TeamActivity extends BasicMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "act_name",nullable = true)
    private String actname;

    @Column(name = "team_type",nullable = false)
    private int teamtype;


    @Column(name = "time_limit",nullable = true)
    private int timelimit;

    @Column(name = "team_price",nullable = true)
    private double teamprice;

    @Column(name = "needer",nullable = true)
    private int needer;


    @Column(name = "goods_name",nullable = true)
    private String goodsname;


    @Column(name = "goods_id",nullable = true)
    private int goodsid;

    @Column(name = "buy_limit",nullable = true)
    private int buylimit;


    @Column(name = "sales_sum",nullable = true)
    private int salessum;



    @Column(name = "share_title",nullable = true)
    private String sharetitle;

    @Column(name = "share_desc",nullable = true)
    private String sharedesc;

    @Column(name = "share_img",nullable = true)
    private String shareimg;




    @Column(name = "sort",nullable = true)
    private int sort;

    @Column(name = "is_recommend",nullable = true)
    private int isrecommend;

    @Column(name = "status",nullable = true)
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getActname() {
        return actname;
    }

    public void setActname(String actname) {
        this.actname = actname;
    }

    public int getTeamtype() {
        return teamtype;
    }

    public void setTeamtype(int teamtype) {
        this.teamtype = teamtype;
    }

    public int getTimelimit() {
        return timelimit;
    }

    public void setTimelimit(int timelimit) {
        this.timelimit = timelimit;
    }

    public double getTeamprice() {
        return teamprice;
    }

    public void setTeamprice(double teamprice) {
        this.teamprice = teamprice;
    }

    public int getNeeder() {
        return needer;
    }

    public void setNeeder(int needer) {
        this.needer = needer;
    }

    public String getGoodsname() {
        return goodsname;
    }

    public void setGoodsname(String goodsname) {
        this.goodsname = goodsname;
    }

    public int getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(int goodsid) {
        this.goodsid = goodsid;
    }

    public int getBuylimit() {
        return buylimit;
    }

    public void setBuylimit(int buylimit) {
        this.buylimit = buylimit;
    }

    public int getSalessum() {
        return salessum;
    }

    public void setSalessum(int salessum) {
        this.salessum = salessum;
    }

    public String getSharetitle() {
        return sharetitle;
    }

    public void setSharetitle(String sharetitle) {
        this.sharetitle = sharetitle;
    }

    public String getSharedesc() {
        return sharedesc;
    }

    public void setSharedesc(String sharedesc) {
        this.sharedesc = sharedesc;
    }

    public String getShareimg() {
        return shareimg;
    }

    public void setShareimg(String shareimg) {
        this.shareimg = shareimg;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getIsrecommend() {
        return isrecommend;
    }

    public void setIsrecommend(int isrecommend) {
        this.isrecommend = isrecommend;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
