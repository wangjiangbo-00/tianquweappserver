package com.ziyoushenghuo.response;


public class WeixinExpreeFeeMessage  extends  BasicMessage
{

    int prefee = 0;
    int expressfee = 0;

    public int getPrefee() {
        return prefee;
    }

    public void setPrefee(int prefee) {
        this.prefee = prefee;
    }

    public int getExpressfee() {
        return expressfee;
    }

    public void setExpressfee(int expressfee) {
        this.expressfee = expressfee;
    }
}
