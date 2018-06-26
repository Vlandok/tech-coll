
package com.vlad.maskaikin.getCity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DependentLocality {

    @SerializedName("DependentLocalityName")
    @Expose
    private String dependentLocalityName;

    public String getDependentLocalityName() {
        return dependentLocalityName;
    }

    public void setDependentLocalityName(String dependentLocalityName) {
        this.dependentLocalityName = dependentLocalityName;
    }

}
