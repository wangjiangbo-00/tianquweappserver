package com.ziyoushenghuo.express;


/**
 *
 * 快递鸟订阅推送2.0接口
 *
 * @技术QQ: 4009633321
 * @技术QQ群: 200121393
 * @see: http://www.kdniao.com/api-subscribe
 * @copyright: 深圳市快金数据技术服务有限公司
 *
 * ID和Key请到官网申请：http://www.kdniao.com/ServiceApply.aspx
 */

public class KdMessage {



    String LogisticCode;

    String ShipperCode;


    KdRole Sender;

    KdRole Receiver;



    public String getLogisticCode() {
        return LogisticCode;
    }

    public void setLogisticCode(String logisticCode) {
        LogisticCode = logisticCode;
    }


    public KdRole getSender() {
        return Sender;
    }

    public void setSender(KdRole sender) {
        Sender = sender;
    }

    public KdRole getReceiver() {
        return Receiver;
    }

    public void setReceiver(KdRole receiver) {
        Receiver = receiver;
    }

    public String getShipperCode() {
        return ShipperCode;
    }

    public void setShipperCode(String shipperCode) {
        ShipperCode = shipperCode;
    }

    public static class KdRole{

        String Name;
        String Mobile;
        String ProvinceName;
        String CityName;
        String Address;
        String ExpAreaName;

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getMobile() {
            return Mobile;
        }

        public void setMobile(String mobile) {
            Mobile = mobile;
        }

        public String getProvinceName() {
            return ProvinceName;
        }

        public void setProvinceName(String provinceName) {
            ProvinceName = provinceName;
        }

        public String getCityName() {
            return CityName;
        }

        public void setCityName(String cityName) {
            CityName = cityName;
        }

        public String getAddress() {
            return Address;
        }

        public void setAddress(String address) {
            Address = address;
        }

        public String getExpAreaName() {
            return ExpAreaName;
        }

        public void setExpAreaName(String expAreaName) {
            ExpAreaName = expAreaName;
        }
    }


}
