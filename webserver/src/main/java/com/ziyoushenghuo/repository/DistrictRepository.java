package com.ziyoushenghuo.repository;



import com.ziyoushenghuo.entry.City;
import com.ziyoushenghuo.entry.District;
import com.ziyoushenghuo.entry.Region;
import com.ziyoushenghuo.entry.Shop;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface DistrictRepository extends JpaRepository<District,Long> {


    District findById(int  id);
    public List<District> findAllByP(int cityid);

    List<District>  findByN(String name);
}