package com.ziyoushenghuo.response;


public class WeixinUserinfoMessage  extends  BasicMessage{

       String phone;
       int balance;
       int balancelock;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

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
}
