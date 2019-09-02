
package com.ziyoushenghuo.common;


import com.ziyoushenghuo.entry.CustomerShipAddr;
import com.ziyoushenghuo.entry.Goods;
import com.ziyoushenghuo.entry.ShopExpressFee;
import com.ziyoushenghuo.service.ShopExpressFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/* 几个运费的封装
  @author 王江波
  @version V1.0
*/
@Component
public class ShipFeeUtils {

    @Autowired
    private ShopExpressFeeService shopExpressFeeService;

    public boolean checkPreFee(Goods goods, int orderfee,int buysum )
    {
        boolean needfee = false;
        int feeid = goods.getFeeid();
        int realshipfee = 0;
        if(feeid == 0)
        {

        }
        else
        {
            ShopExpressFee shopExpressFee =  shopExpressFeeService.getById(feeid);

            if(shopExpressFee!=null)
            {

                int weight = goods.getWeight();
                int xweight = (weight - shopExpressFee.getWeight_snum())/shopExpressFee.getWeight_xnum();
                int moreweight = (weight - shopExpressFee.getWeight_snum())%shopExpressFee.getWeight_xnum() == 0?0:1;
                int fee = shopExpressFee.getWeight_sprice() +
                        shopExpressFee.getWeight_xprice()*(xweight+ moreweight);
                realshipfee = fee;

            }
        }



        if(orderfee == realshipfee*buysum)
        {
            return  true;
        }
        else
        {
            return  false;
        }
    }


    public int getShipFee(Goods goods,int provinceid)
    {
        boolean needfee = false;
        int feeid = goods.getFeeid();
        int realshipfee = 0;
        if(feeid == 0)
        {

        }
        else
        {
            ShopExpressFee shopExpressFee =  shopExpressFeeService.getById(feeid);

            if(shopExpressFee!=null)
            {

                int weight = goods.getWeight();
                String feeArr = shopExpressFee.getProvince_id_array();
                String[] provincearr = feeArr.split(",");

                for(String province :provincearr)
                {

                    int provincei = Integer.valueOf(province);
                    if(provincei == provinceid){
                        int xweight = (weight - shopExpressFee.getWeight_snum())/shopExpressFee.getWeight_xnum();
                        int moreweight = (weight - shopExpressFee.getWeight_snum())%shopExpressFee.getWeight_xnum() == 0?0:1;
                        int fee = shopExpressFee.getWeight_sprice() +
                                shopExpressFee.getWeight_xprice()*(xweight+ moreweight);
                        realshipfee = fee;

                        break;
                    }
                }

            }
        }




        return  realshipfee;
    }

    public boolean checkExpressFee(Goods goods, CustomerShipAddr customerShipAddr, int orderfee,int buysum)
    {
        boolean needfee = false;
        int feeid = goods.getFeeid();
        int realshipfee = 0;
        if(feeid == 0)
        {

        }
        else
        {
            ShopExpressFee shopExpressFee =  shopExpressFeeService.getById(feeid);
            if(shopExpressFee!=null)
            {
                String feeArr = shopExpressFee.getProvince_id_array();
                String[] provincearr = feeArr.split(",");

                for(String province :provincearr)
                {
                    int weight = goods.getWeight();
                    int provincei = Integer.valueOf(province);
                    if(provincei == customerShipAddr.getProvince()){
                        int xweight = (weight - shopExpressFee.getWeight_snum())/shopExpressFee.getWeight_xnum();
                        int moreweight = (weight - shopExpressFee.getWeight_snum())%shopExpressFee.getWeight_xnum() == 0?0:1;
                        int fee = shopExpressFee.getWeight_sprice() +
                                shopExpressFee.getWeight_xprice()*(xweight+ moreweight);
                        realshipfee = fee;

                        break;
                    }
                }

            }
        }

        if(orderfee == realshipfee*buysum)
        {
            return  true;
        }
        else
        {
            return  false;
        }
    }
}


