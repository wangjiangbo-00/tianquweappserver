package com.ziyoushenghuo.entry;

import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "zytc_gift_participate")
public class GiftParticipate extends BasicMessage {


    public static final int PARTICIPATE_ACTION_APPOINTMENT = 1;
    public static final int PARTICIPATE_ACTION_ALREADY_IN = 2;


    public static final int PARTICIPATE_RESULT_NOMAL = 0;
    public static final int PARTICIPATE_RESULT_SUCCESS = 1;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "giftid")
    private int giftid;

    @Column(name = "userid")
    private int userid;

    @Column(name = "status")
    private int status;

    @Column(name = "relaystatus")
    private int relaystatus;

    @Column(name = "result")
    private int result;

    @Column(name = "mynumber")
    private int mynumber;

    @Column(name = "formid")
    private String formid;


    @Column(name = "relaycount")
    private int relaycount;


    @Column(name = "relayuserid")
    private int relayuserid;


    @Column(name = "relayname")
    private String relayname;


    @Column(name = "giftname")
    private String giftname;


    @Column(name = "participatetime")
    private Date participatetime;


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

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }


    public String getFormid() {
        return formid;
    }

    public void setFormid(String formid) {
        this.formid = formid;
    }

    public int getMynumber() {
        return mynumber;
    }

    public void setMynumber(int mynumber) {
        this.mynumber = mynumber;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public int getRelaycount() {
        return relaycount;
    }

    public void setRelaycount(int relaycount) {
        this.relaycount = relaycount;
    }

    public int getRelayuserid() {
        return relayuserid;
    }

    public void setRelayuserid(int relayuserid) {
        this.relayuserid = relayuserid;
    }

    public String getRelayname() {
        return relayname;
    }

    public void setRelayname(String relayname) {
        this.relayname = relayname;
    }


    public int getRelaystatus() {
        return relaystatus;
    }

    public void setRelaystatus(int relaystatus) {
        this.relaystatus = relaystatus;
    }


    public String getGiftname() {
        return giftname;
    }

    public void setGiftname(String giftname) {
        this.giftname = giftname;
    }

    public Date getParticipatetime() {
        return participatetime;
    }

    public void setParticipatetime(Date participatetime) {
        this.participatetime = participatetime;
    }
}
