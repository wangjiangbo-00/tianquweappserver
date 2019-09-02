package com.ziyoushenghuo.repository;



import com.ziyoushenghuo.entry.City;
import com.ziyoushenghuo.entry.OrderExpress;
import com.ziyoushenghuo.entry.OrderExtra;
import com.ziyoushenghuo.entry.Shop;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface OrderExtraRepository extends JpaRepository<OrderExtra,Long> {


    OrderExtra findById(long  id);

}