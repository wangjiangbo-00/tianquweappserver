package com.ziyoushenghuo.response;


public class LotteryParticipateMessage  extends  BasicMessage
{

    int type;

    int result;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
