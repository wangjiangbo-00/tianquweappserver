package com.ziyoushenghuo.response;


import com.ziyoushenghuo.entry.Customer;
import com.ziyoushenghuo.entry.Goods;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.entry.TeamFounder;

import java.util.List;

public class SuperGroupDetailsMessage  extends  BasicMessage
{


    int orderid;
    int people;

    TeamFounder teamFounder;

    public int getOrderid() {
        return orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public TeamFounder getTeamFounder() {
        return teamFounder;
    }

    public void setTeamFounder(TeamFounder teamFounder) {
        this.teamFounder = teamFounder;
    }
}
