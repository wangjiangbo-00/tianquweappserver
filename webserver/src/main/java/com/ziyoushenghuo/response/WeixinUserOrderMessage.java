package com.ziyoushenghuo.response;




import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class WeixinUserOrderMessage  extends  BasicMessage{

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<OrderInfo> orders;
    int count;
    int total;
    public  static class OrderInfo{

        int orderid;
        int totalmoney;
        int totalpower;
        int totaltime;
        String ordertime;
        int orderstatus;
        String deviceInfo;

        public int getOrderid() {
            return orderid;
        }

        public void setOrderid(int orderid) {
            this.orderid = orderid;
        }

        public int getTotalmoney() {
            return totalmoney;
        }

        public void setTotalmoney(int totalmoney) {
            this.totalmoney = totalmoney;
        }

        public int getTotalpower() {
            return totalpower;
        }

        public void setTotalpower(int totalpower) {
            this.totalpower = totalpower;
        }

        public int getTotaltime() {
            return totaltime;
        }

        public void setTotaltime(int totaltime) {
            this.totaltime = totaltime;
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

        public String getDeviceInfo() {
            return deviceInfo;
        }

        public void setDeviceInfo(String deviceInfo) {
            this.deviceInfo = deviceInfo;
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


