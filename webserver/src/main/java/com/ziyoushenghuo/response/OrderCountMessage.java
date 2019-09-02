package com.ziyoushenghuo.response;




import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class OrderCountMessage  extends  BasicMessage{


    int unpay;
    int ungroup;
    int unship;
    int unreceive;

    public int getUnpay() {
        return unpay;
    }

    public void setUnpay(int unpay) {
        this.unpay = unpay;
    }

    public int getUngroup() {
        return ungroup;
    }

    public void setUngroup(int ungroup) {
        this.ungroup = ungroup;
    }

    public int getUnship() {
        return unship;
    }

    public void setUnship(int unship) {
        this.unship = unship;
    }

    public int getUnreceive() {
        return unreceive;
    }

    public void setUnreceive(int unreceive) {
        this.unreceive = unreceive;
    }
}


