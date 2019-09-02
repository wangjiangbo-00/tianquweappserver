package com.ziyoushenghuo.service;

import com.ziyoushenghuo.entry.Owner;
import com.ziyoushenghuo.repository.OwnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * Created by RXJ on 2016/9/9.
 * 基本用户
 */
@Service

public class OwnerService {

    @Autowired
    private OwnerRepository ownerRepository;

    /**
     * 获取待验证用户信息
     */
    public Owner getOwner(String param){
        return ownerRepository.findByUsername(param);
    }

    public Owner getOwnerById(int id){
        return ownerRepository.findById(id);
    }
    public Owner getOwnerByWeixin(String weixinid){
        return ownerRepository.findByWeixin(weixinid);
    }
    public Boolean checkOwnerByUsername(String username){
        Owner owner=ownerRepository.findByUsername(username);
        Boolean r=false;
        if(owner!=null){
            r=true;
        }
        return r;
    }
    public Owner getOwnerByUser(String username){
        return ownerRepository.findByUsername(username);
    }

    public Boolean getOwnerByLogin(String username,String password){
        Owner owner=ownerRepository.findByUsernameAndPassword(username,password);
        Boolean r=false;
        if(owner!=null){
            r=true;
        }
        return r;
    }

   // public Owner getOwner(String username,String password){
      // Owner owner=ownerRepository.findByUsernameAndPassword(username,password);
        //return owner;
    //}


    public void create(Owner owner)
    {
      Owner owner1= ownerRepository.save(owner);

        ownerRepository.save(owner1);
    }



    public void update(Owner owner) {
        ownerRepository.save(owner);
    }
    public void create1(Owner owner1,Owner owner2)
    {

        ownerRepository.save(owner1);
        ownerRepository.save(owner2);
    }
}
