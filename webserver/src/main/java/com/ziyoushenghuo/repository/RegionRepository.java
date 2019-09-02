package com.ziyoushenghuo.repository;
import com.ziyoushenghuo.entry.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by WWJ on 2017/12/10.
 */
@Repository
public interface RegionRepository extends JpaRepository<Region,Long> {
    List<Region> findByParentid(int parentid);
    List<Region> findByLevel(int level);
    Region findById(int id);
    List<Region> findAll();
}