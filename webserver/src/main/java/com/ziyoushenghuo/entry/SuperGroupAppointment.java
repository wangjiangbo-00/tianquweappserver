package com.ziyoushenghuo.entry;

import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "zytc_supergroup_appointment")
public class SuperGroupAppointment extends BasicMessage {

    public static final int PARTICIPATE_ACTION_APPOINTMENT = 1;
    public static final int PARTICIPATE_ACTION_NOMAL = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "groupid")
    private int groupid;

    @Column(name = "userid")
    private int userid;

    @Column(name = "status")
    private int status;



    @Column(name = "formid")
    private String formid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFormid() {
        return formid;
    }

    public void setFormid(String formid) {
        this.formid = formid;
    }
}
