package com.ntuproject.fast_and_furious;

import java.util.List;

public class Response {
    String odata;
    List<value> valueList;

    public List<value> getValueList() {
        return valueList;
    }

    public String getOdata() {
        return odata;
    }

    public void setOdata(String odata) {
        this.odata = odata;
    }

    public void setValueList(List<value> valueList) {
        this.valueList = valueList;
    }
}
