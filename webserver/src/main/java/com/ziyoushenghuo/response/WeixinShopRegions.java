package com.ziyoushenghuo.response;


import com.ziyoushenghuo.entry.City;
import com.ziyoushenghuo.entry.District;
import com.ziyoushenghuo.entry.Province;
import com.ziyoushenghuo.entry.Shop;

import java.util.List;

public class WeixinShopRegions extends  BasicMessage{

      int provinceid;
      String provincename;
      List<Shop> cityList;

    public int getProvinceid() {
        return provinceid;
    }

    public void setProvinceid(int provinceid) {
        this.provinceid = provinceid;
    }

    public String getProvincename() {
        return provincename;
    }

    public void setProvincename(String provincename) {
        this.provincename = provincename;
    }

    public List<Shop> getCityList() {
        return cityList;
    }

    public void setCityList(List<Shop> cityList) {
        this.cityList = cityList;
    }
}
