package com.orientsec.grpc.demo;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * @date: 2020/7/30 16:35
 * @author: farui.yu
 */
public class HelloRequestMo {

    @Size(min = 5)
    private String nameBar;
    private List<String> ageList;
    private String wantToFly;

    public String getNameBar() {
        return nameBar;
    }

    public void setNameBar(String nameBar) {
        this.nameBar = nameBar;
    }

    public List<String> getAgeList() {
        return ageList;
    }

    public void setAgeList(List<String> ageList) {
        this.ageList = ageList;
    }

    public String getWantToFly() {
        return wantToFly;
    }

    public void setWantToFly(String wantToFly) {
        this.wantToFly = wantToFly;
    }
}
