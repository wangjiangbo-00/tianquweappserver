package com.ziyoushenghuo.entry;

import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;


@Entity
@Table(name = "sys_city")
public class City extends BasicMessage{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "city_id", nullable = false)
    private int id;

    @Column(name = "city_name")
    private String n;

    @Column(name = "province_id")
    private int p;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }
}
