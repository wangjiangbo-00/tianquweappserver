package com.ziyoushenghuo.response;




import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class PageableMessage<T>  extends  BasicMessage{

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<T> list;
    int count;
    int total;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}


