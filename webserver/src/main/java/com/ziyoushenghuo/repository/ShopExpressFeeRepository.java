package com.ziyoushenghuo.repository;



import com.ziyoushenghuo.entry.ShopExpressFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface ShopExpressFeeRepository extends JpaRepository<ShopExpressFee,Long> {

    ShopExpressFee findById(int id);

    List<ShopExpressFee> findByShopid(int shopid);




}