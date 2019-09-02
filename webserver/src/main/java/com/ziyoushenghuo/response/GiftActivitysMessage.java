package com.ziyoushenghuo.response;


import com.ziyoushenghuo.entry.*;

import java.util.List;

public class GiftActivitysMessage  extends  BasicMessage
{


    List<GiftActivity> giftActivities;
    List<GiftParticipate> giftParticipates;

    public List<GiftActivity> getGiftActivities() {
        return giftActivities;
    }

    public void setGiftActivities(List<GiftActivity> giftActivities) {
        this.giftActivities = giftActivities;
    }

    public List<GiftParticipate> getGiftParticipates() {
        return giftParticipates;
    }

    public void setGiftParticipates(List<GiftParticipate> giftParticipates) {
        this.giftParticipates = giftParticipates;
    }
}
