package com.ziyoushenghuo.repository;

import java.util.List;


import com.ziyoushenghuo.entry.Withdrew;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface WithdrewRepository extends JpaRepository<Withdrew,Long> {



    Withdrew findById(int id);
    List<Withdrew> findByShopid(int shopid);

    List<Withdrew> findByShopid(int shopid, Pageable pageable);

    List<Withdrew> findByStatus(int status, Pageable pageable);
}