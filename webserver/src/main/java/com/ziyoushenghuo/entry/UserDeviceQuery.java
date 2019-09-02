package com.ziyoushenghuo.entry;


        import javax.persistence.*;


@Entity
@Table(name = "zdw_userdevice")
public class UserDeviceQuery {

    @Id
    @Column(name = "id",nullable = false)
    private int id;

    @Column(name = "templateid",nullable = false)
    private int templateid;

    @Column(name = "name",nullable = true)
    private String name;

    @Column(name = "positionid",nullable = true)
    private int positionid;

    @Column(name = "qrposter",nullable = true)
    private String qrposter;

    @Column(name = "ownerid",nullable = false)
    private int ownerid;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTemplateid() {
        return templateid;
    }

    public void setTemplateid(int templateid) {
        this.templateid = templateid;
    }

    public int getPositionid() {
        return positionid;
    }

    public void setPositionid(int positionid) {
        this.positionid = positionid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQrposter() {
        return qrposter;
    }

    public void setQrposter(String qrposter) {
        this.qrposter = qrposter;
    }

    public int getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(int ownerid) {
        this.ownerid = ownerid;
    }



}
