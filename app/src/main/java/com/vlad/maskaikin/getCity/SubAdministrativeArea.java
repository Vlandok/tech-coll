
package com.vlad.maskaikin.getCity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubAdministrativeArea {

    @SerializedName("SubAdministrativeAreaName")
    @Expose
    private String subAdministrativeAreaName;
    @SerializedName("Locality")
    @Expose
    private Locality locality;

    public String getSubAdministrativeAreaName() {
        return subAdministrativeAreaName;
    }

    public void setSubAdministrativeAreaName(String subAdministrativeAreaName) {
        this.subAdministrativeAreaName = subAdministrativeAreaName;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

}
