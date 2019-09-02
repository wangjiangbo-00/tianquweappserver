package com.ziyoushenghuo.response;


import com.ziyoushenghuo.entry.*;

import java.util.List;

public class GiftActivityDetailsMessage  extends  BasicMessage
{


    List<String> personas;
    int people;

    GiftActivity giftActivity;

    public List<String> getPersonas() {
        return personas;
    }

    public void setPersonas(List<String> personas) {
        this.personas = personas;
    }

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public GiftActivity getGiftActivity() {
        return giftActivity;
    }

    public void setGiftActivity(GiftActivity giftActivity) {
        this.giftActivity = giftActivity;
    }
}
