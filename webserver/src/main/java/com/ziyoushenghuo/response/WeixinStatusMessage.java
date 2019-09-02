package com.ziyoushenghuo.response;


public class WeixinStatusMessage  extends  BasicMessage
{
    public static final int WEIXIN_STATUS_UNKNOWN = 0;
    public  static final int WEIXIN_STATUS_TO_SCAN = 1;
    public static final int WEIXIN_STATUS_SCAN_DEVICE = 2;
    public static final int WEIXIN_STATUS_CHARGEPROCESS = 3;
    public static final int WEIXIN_STATUS_TO_PAY = 4;
   int money;
   int deviceid;

   String ordertime = "";

   int volt ;
   int current ;
   int power;

   int currentpower;

   int chargetime;
   int chargemoney;

   int billid ;


   int status;

    String name = "";

    int commstatus;
    int deviceStatus;
    int chargeStatus;

    int chargeProcessStatus;
    boolean busedByMe;



    String ownerName = "";
    String phone = "";


    int mode = -1;
    int count = -1;
    String splits = "";

    int orderid;

    boolean personal = false;

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(int deviceid) {
        this.deviceid = deviceid;
    }

    public String getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(String ordertime) {
        this.ordertime = ordertime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getVolt() {
        return volt;
    }

    public void setVolt(int volt) {
        this.volt = volt;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getChargetime() {
        return chargetime;
    }

    public void setChargetime(int chargetime) {
        this.chargetime = chargetime;
    }

    public int getChargemoney() {
        return chargemoney;
    }

    public void setChargemoney(int chargemoney) {
        this.chargemoney = chargemoney;
    }

    public int getBillid() {
        return billid;
    }

    public void setBillid(int billid) {
        this.billid = billid;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCommstatus() {
        return commstatus;
    }

    public void setCommstatus(int commstatus) {
        this.commstatus = commstatus;
    }

    public int getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(int deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public int getChargeStatus() {
        return chargeStatus;
    }

    public void setChargeStatus(int chargeStatus) {
        this.chargeStatus = chargeStatus;
    }

    public int getChargeProcessStatus() {
        return chargeProcessStatus;
    }

    public void setChargeProcessStatus(int chargeProcessStatus) {
        this.chargeProcessStatus = chargeProcessStatus;
    }

    public boolean isBusedByMe() {
        return busedByMe;
    }

    public void setBusedByMe(boolean busedByMe) {
        this.busedByMe = busedByMe;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getSplits() {
        return splits;
    }

    public void setSplits(String splits) {
        this.splits = splits;
    }

    public int getOrderid() {
        return orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public int getCurrentpower() {
        return currentpower;
    }

    public void setCurrentpower(int currentpower) {
        this.currentpower = currentpower;
    }

    public boolean isPersonal() {
        return personal;
    }

    public void setPersonal(boolean personal) {
        this.personal = personal;
    }
}
