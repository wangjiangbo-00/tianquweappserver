package com.ziyoushenghuo.response;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class ResponseContainer {

    public static final String RESPONSE_NO_ERROR = "OK";
    public static final int RESPONSE_CODE_OK = 0;
    @JsonInclude(JsonInclude.Include.NON_NULL)
     BasicMessage message;
     int returncode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
     String errormsg;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<? extends Object> lists;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    String extramsg;

    public BasicMessage getMessage() {
        return message;
    }

    public void setMessage(BasicMessage message) {
        this.message = message;
    }

    public int getReturncode() {
        return returncode;
    }

    public void setReturncode(int returncode) {
        this.returncode = returncode;
    }

    public String getErrormsg() {
        return errormsg;
    }

    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg;
    }


    public List<? extends Object> getLists() {
        return lists;
    }

    public void setLists(List<? extends Object> lists) {
        this.lists = lists;
    }

    public String getExtramsg() {
        return extramsg;
    }

    public void setExtramsg(String extramsg) {
        this.extramsg = extramsg;
    }
}
