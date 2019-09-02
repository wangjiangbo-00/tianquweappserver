package com.ziyoushenghuo.entry;





import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "zytc_order_extra")
public class OrderExtra {

    @Id
    @Column(name = "order_id", nullable = false)
    private Long id;

    @Column(name = "buyer_message")
    private String buyer_message;
    @Column(name = "blessing_message")
    private String blessing_message;

    @Column(name = "articleid")
    private int articleid;

    @Column(name = "fixaddr")
    private int fixaddr;

    @Column(name = "tryrefund")
    private int tryrefund;


    @Column(name = "sendnickname")
    private String sendnickname;

    @Column(name = "sendheadpic")
    private String sendheadpic;

    @Column(name = "recievenickname")
    private String recievenickname;

    @Column(name = "recieveheadpic")
    private String recieveheadpic;


    @Column(name = "givertime")
    private Date givertime;


    @Column(name = "formid")
    private String formid;

    @Column(name = "refusemsg")
    private String refusemsg;

    public String getRefusemsg() {
        return refusemsg;
    }

    public void setRefusemsg(String refusemsg) {
        this.refusemsg = refusemsg;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBuyer_message() {
        return buyer_message;
    }

    public void setBuyer_message(String buyer_message) {
        this.buyer_message = buyer_message;
    }

    public String getBlessing_message() {
        return blessing_message;
    }

    public void setBlessing_message(String blessing_message) {
        this.blessing_message = blessing_message;
    }

    public int getArticleid() {
        return articleid;
    }

    public void setArticleid(int articleid) {
        this.articleid = articleid;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public int getFixaddr() {
        return fixaddr;
    }

    public void setFixaddr(int fixaddr) {
        this.fixaddr = fixaddr;
    }

    public int getTryrefund() {
        return tryrefund;
    }

    public void setTryrefund(int tryrefund) {
        this.tryrefund = tryrefund;
    }


    public String getSendnickname() {
        return sendnickname;
    }

    public void setSendnickname(String sendnickname) {
        this.sendnickname = sendnickname;
    }

    public String getSendheadpic() {
        return sendheadpic;
    }

    public void setSendheadpic(String sendheadpic) {
        this.sendheadpic = sendheadpic;
    }

    public String getRecievenickname() {
        return recievenickname;
    }

    public void setRecievenickname(String recievenickname) {
        this.recievenickname = recievenickname;
    }

    public String getRecieveheadpic() {
        return recieveheadpic;
    }

    public void setRecieveheadpic(String recieveheadpic) {
        this.recieveheadpic = recieveheadpic;
    }


    public Date getGivertime() {
        return givertime;
    }

    public void setGivertime(Date givertime) {
        this.givertime = givertime;
    }


    public String getFormid() {
        return formid;
    }

    public void setFormid(String formid) {
        this.formid = formid;
    }
}
