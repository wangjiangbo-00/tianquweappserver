package com.ziyoushenghuo.entry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;


@Entity
@Table(name = "zytc_member_express_address")
public class CustomerShipAddr extends BasicMessage {

    public static final int ADDR_NOMAL = 0;
    public static final int ADDR_DEFAULT = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)

    private int id;


    @Column(name = "uid",nullable = false)
    private int uid;


    @Column(name = "consigner",nullable = false)
    private String consigner;


    @Column(name = "mobile",nullable = false)
    private String mobile;



    @Column(name = "province",nullable = false)
    private int province;


    @Column(name = "city")
    private int city;

    @Column(name = "district")
    private int district;


    @Column(name = "address")
    private String address;

    @Column(name = "zip_code")
    private String zip_code;

    @Column(name = "is_default")
    private int isdefault;


    @Column(name = "provincename",nullable = true)
    private String provincename;


    @Column(name = "cityname",nullable = true)
    private String cityname;

    @Column(name = "districtname",nullable = true)
    private String districtname;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getConsigner() {
        return consigner;
    }

    public void setConsigner(String consigner) {
        this.consigner = consigner;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getProvince() {
        return province;
    }

    public void setProvince(int province) {
        this.province = province;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public int getDistrict() {
        return district;
    }

    public void setDistrict(int district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public int getIsdefault() {
        return isdefault;
    }

    public void setIsdefault(int isdefault) {
        this.isdefault = isdefault;
    }

    public String getProvincename() {
        return provincename;
    }

    public void setProvincename(String provincename) {
        this.provincename = provincename;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getDistrictname() {
        return districtname;
    }

    public void setDistrictname(String districtname) {
        this.districtname = districtname;
    }
}
