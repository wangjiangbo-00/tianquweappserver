package com.ziyoushenghuo.response;


import java.util.Date;
import java.util.List;

public class OrderExpressMessage extends  BasicMessage{

    String shipper;
    String tracking_number;
    List<ExpressTrace>traces;
    String errmsg;



    public String getShipper() {
        return shipper;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    public String getTracking_number() {
        return tracking_number;
    }

    public void setTracking_number(String tracking_number) {
        this.tracking_number = tracking_number;
    }

    public List<ExpressTrace> getTraces() {
        return traces;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public void setTraces(List<ExpressTrace> traces) {
        this.traces = traces;
    }

    public  static  class ExpressTrace{
        String time;
        String address;
        String remark;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
