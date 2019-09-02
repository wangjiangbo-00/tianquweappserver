package com.ziyoushenghuo.response;




import com.fasterxml.jackson.annotation.JsonInclude;
import com.ziyoushenghuo.entry.Region;

import java.util.List;

public class WeixinOwnerRegionMessage  extends  BasicMessage{

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<Region> regions;

    public List<Region> getRegions() {
        return regions;
    }

    public void setRegions(List<Region> regions) {
        this.regions = regions;
    }
}


