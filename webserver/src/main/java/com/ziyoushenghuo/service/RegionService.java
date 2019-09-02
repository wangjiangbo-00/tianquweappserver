package com.ziyoushenghuo.service;

import com.ziyoushenghuo.entry.City;
import com.ziyoushenghuo.entry.District;
import com.ziyoushenghuo.entry.Province;
import com.ziyoushenghuo.entry.Region;
import com.ziyoushenghuo.repository.CityRepository;
import com.ziyoushenghuo.repository.DistrictRepository;
import com.ziyoushenghuo.repository.ProvinceRepository;
import com.ziyoushenghuo.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by RXJ on 2016/9/9.
 * 基本用户
 */
@Service

public class RegionService {

    private final int LEVEL_PROVINCE = 1;
    private final int LEVEL_CITY = 2;
    private final int LEVEL_DISTRICTS = 3;

    @Autowired
    private RegionRepository regionRepository;


    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private DistrictRepository districtRepository;

    public List<Province> GetAllProvince(){return  provinceRepository.findAll();}

    public List<Region> GetAll(){return  regionRepository.findAll();}

    public List<City> GetAllProvinceCity(int provinceid){return  cityRepository.findAllByP(provinceid);}

    public List<City> GetAllCity(){return  cityRepository.findAll();}

    public List<District> GetAllDistrict(){return  districtRepository.findAll();}

    public List<District> GetAllCityDistricts(int cityid){return  districtRepository.findAllByP(cityid);}

    public void createOrUpdate(Region region)
    {
        regionRepository.save(region);
    }

    public void deleteRegion(Region region) {
        regionRepository.delete(region);
    }


    public Province findProviceByname(String province)
    {
        return  provinceRepository.findByN(province);
    }

    public City findCityByname(String city)
    {
        return  cityRepository.findByN(city);
    }

    public List<District>  findDistrictByname(String district)
    {
        return  districtRepository.findByN(district);
    }


    public void addProvince(Province province)
    {
        provinceRepository.save(province);
    }

    public void addCity(City city)
    {
        cityRepository.save(city);
    }

    public void addDistrict(District district)
    {
        districtRepository.save(district);
    }
}
