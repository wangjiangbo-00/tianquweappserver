package com.ziyoushenghuo.entry;

import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "zytc_gift_winner")
public class GiftWinner extends BasicMessage {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "giftid")
    private int giftid;

    @Column(name = "userid")
    private int userid;

    @Column(name = "giftname")
    private String giftname;


    @Column(name = "wintime")
    private Date wintime;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGiftid() {
        return giftid;
    }

    public void setGiftid(int giftid) {
        this.giftid = giftid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }


    public String getGiftname() {
        return giftname;
    }

    public void setGiftname(String giftname) {
        this.giftname = giftname;
    }

    public Date getWintime() {
        return wintime;
    }

    public void setWintime(Date wintime) {
        this.wintime = wintime;
    }
}
