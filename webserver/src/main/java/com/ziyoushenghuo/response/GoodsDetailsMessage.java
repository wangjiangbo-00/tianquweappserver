package com.ziyoushenghuo.response;


import com.ziyoushenghuo.entry.*;

import java.util.List;

public class GoodsDetailsMessage  extends  BasicMessage
{


    private List<String> gallery;


    private List<GoodsSku> goodsSkuList;

    Goods goods;

    public List<String> getGallery() {
        return gallery;
    }

    public void setGallery(List<String> gallery) {
        this.gallery = gallery;
    }

    public List<GoodsSku> getGoodsSkuList() {
        return goodsSkuList;
    }

    public void setGoodsSkuList(List<GoodsSku> goodsSkuList) {
        this.goodsSkuList = goodsSkuList;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }
}
