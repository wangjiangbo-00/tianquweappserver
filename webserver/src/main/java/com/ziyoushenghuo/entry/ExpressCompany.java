package com.ziyoushenghuo.entry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "zytc_express_company")
public class ExpressCompany extends BasicMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "co_id", nullable = false)
    private int id;


    @Column(name = "company_name",nullable = false)
    private String company_name;


    @Column(name = "kdncode",nullable = false)
    private String kdncode;

    @Column(name = "shiptype",nullable = false)
    private int shiptype;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getKdncode() {
        return kdncode;
    }

    public void setKdncode(String kdncode) {
        this.kdncode = kdncode;
    }

    public int getShiptype() {
        return shiptype;
    }

    public void setShiptype(int shiptype) {
        this.shiptype = shiptype;
    }
}
