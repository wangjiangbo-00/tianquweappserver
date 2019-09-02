package com.ziyoushenghuo.response;


public class WeixinUserRefundInfo  extends  BasicMessage{


       int balance;
       int balancelock;

       int ordermoney;
       int money;

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getBalancelock() {
        return balancelock;
    }

    public void setBalancelock(int balancelock) {
        this.balancelock = balancelock;
    }

    public int getOrdermoney() {
        return ordermoney;
    }

    public void setOrdermoney(int ordermoney) {
        this.ordermoney = ordermoney;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }
}
