package com.ziyoushenghuo.response;


import com.ziyoushenghuo.entry.*;

import java.util.List;




public class PlatformActivityShowsMessage  extends  BasicMessage
{

    public  static class PlatformDiscount{

        PromotionDiscount promotionDiscount;

        List<PromotionDiscountGoods> promotionDiscountGoods;


        public PromotionDiscount getPromotionDiscount() {
            return promotionDiscount;
        }

        public void setPromotionDiscount(PromotionDiscount promotionDiscount) {
            this.promotionDiscount = promotionDiscount;
        }

        public List<PromotionDiscountGoods> getPromotionDiscountGoods() {
            return promotionDiscountGoods;
        }

        public void setPromotionDiscountGoods(List<PromotionDiscountGoods> promotionDiscountGoods) {
            this.promotionDiscountGoods = promotionDiscountGoods;
        }
    }


    List<GiftActivity> giftActivities;
    List<TeamFounder> teamFounders;
    List<PlatformDiscount> platformDiscounts;

    public List<GiftActivity> getGiftActivities() {
        return giftActivities;
    }

    public void setGiftActivities(List<GiftActivity> giftActivities) {
        this.giftActivities = giftActivities;
    }

    public List<TeamFounder> getTeamFounders() {
        return teamFounders;
    }

    public void setTeamFounders(List<TeamFounder> teamFounders) {
        this.teamFounders = teamFounders;
    }

    public List<PlatformDiscount> getPlatformDiscounts() {
        return platformDiscounts;
    }

    public void setPlatformDiscounts(List<PlatformDiscount> platformDiscounts) {
        this.platformDiscounts = platformDiscounts;
    }
}
