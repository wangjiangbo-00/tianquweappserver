package com.ziyoushenghuo.response;




import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class WeixinCustomerRechargeMessage  extends  BasicMessage{

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<OrderInfo> orders;
    int count;
    int total;
    public  static class OrderInfo{

        int id;

        int refid;
        int money;

        String ordertime;
        int orderstatus;

        int type;




        int money_before;
        int money_after;

        String extra;


        public int getRefid() {
            return refid;
        }

        public void setRefid(int refid) {
            this.refid = refid;
        }

        public int getMoney() {
            return money;
        }

        public void setMoney(int money) {
            this.money = money;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getMoney_before() {
            return money_before;
        }

        public void setMoney_before(int money_before) {
            this.money_before = money_before;
        }

        public int getMoney_after() {
            return money_after;
        }

        public void setMoney_after(int money_after) {
            this.money_after = money_after;
        }


        public String getOrdertime() {
            return ordertime;
        }

        public void setOrdertime(String ordertime) {
            this.ordertime = ordertime;
        }

        public int getOrderstatus() {
            return orderstatus;
        }

        public void setOrderstatus(int orderstatus) {
            this.orderstatus = orderstatus;
        }


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }


        public String getExtra() {
            return extra;
        }

        public void setExtra(String extra) {
            this.extra = extra;
        }
    }

    public List<OrderInfo> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderInfo> orders) {
        this.orders = orders;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}


