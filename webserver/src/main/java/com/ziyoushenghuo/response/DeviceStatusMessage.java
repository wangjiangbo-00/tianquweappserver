package com.ziyoushenghuo.response;


public class DeviceStatusMessage  extends  BasicMessage{

       String name;
       int deviceid;
       int commstatus;
       int deviceStatus;
       int chargeStatus;

       int chargeProcessStatus;
       boolean busedByMe;

       int volt;
       int current;
       int power;

       String ownerName = "";
       String phone = "";


       int mode = -1;
       int count = -1;
       String splits = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(int deviceid) {
        this.deviceid = deviceid;
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
}
