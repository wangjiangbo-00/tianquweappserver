package com.ziyoushenghuo.entry;





import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "zytc_order_addr")
public class OrderAddr extends BasicMessage {

    @Id

    @Column(name = "order_id", nullable = false)
    private Long id;

    @Column(name = "receiver_mobile",nullable = true)
    private String receiverMobile;

    @Column(name = "receiver_province",nullable = true)
    private int receiverProvince;

    @Column(name = "receiver_city",nullable = true)
    private int receiverCity;

    @Column(name = "receiver_district")
    private int receiverDistrict;

    @Column(name = "receiver_address")
    private String receiverAddress;

    @Column(name = "receiver_zip")
    private String receiverZip;

    @Column(name = "receiver_name",nullable = false)
    private String receiverName;

    @Column(name = "provincename")
    private String provincename;

    @Column(name = "cityname")
    private String cityname;

    @Column(name = "districtname")
    private String districtname;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public int getReceiverProvince() {
        return receiverProvince;
    }

    public void setReceiverProvince(int receiverProvince) {
        this.receiverProvince = receiverProvince;
    }

    public int getReceiverCity() {
        return receiverCity;
    }

    public void setReceiverCity(int receiverCity) {
        this.receiverCity = receiverCity;
    }

    public int getReceiverDistrict() {
        return receiverDistrict;
    }

    public void setReceiverDistrict(int receiverDistrict) {
        this.receiverDistrict = receiverDistrict;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverZip() {
        return receiverZip;
    }

    public void setReceiverZip(String receiverZip) {
        this.receiverZip = receiverZip;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
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
