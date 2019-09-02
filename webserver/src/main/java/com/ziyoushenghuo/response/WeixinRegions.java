package com.ziyoushenghuo.response;


import com.ziyoushenghuo.entry.City;
import com.ziyoushenghuo.entry.District;
import com.ziyoushenghuo.entry.Province;

import java.util.List;

public class WeixinRegions extends  BasicMessage{

      List<Province> provinceList;
      List<City> cityList;
      List<District> districtList;

    public List<Province> getProvinceList() {
        return provinceList;
    }

    public void setProvinceList(List<Province> provinceList) {
        this.provinceList = provinceList;
    }

    public List<City> getCityList() {
        return cityList;
    }

    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }

    public List<District> getDistrictList() {
        return districtList;
    }

    public void setDistrictList(List<District> districtList) {
        this.districtList = districtList;
    }
}
