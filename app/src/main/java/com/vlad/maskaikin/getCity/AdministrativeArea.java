
package com.vlad.maskaikin.getCity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdministrativeArea {

    @SerializedName("AdministrativeAreaName")
    @Expose
    private String administrativeAreaName;
    @SerializedName("SubAdministrativeArea")
    @Expose
    private SubAdministrativeArea subAdministrativeArea;

    public String getAdministrativeAreaName() {
        return administrativeAreaName;
    }

    public void setAdministrativeAreaName(String administrativeAreaName) {
        this.administrativeAreaName = administrativeAreaName;
    }

    public SubAdministrativeArea getSubAdministrativeArea() {
        return subAdministrativeArea;
    }

    public void setSubAdministrativeArea(SubAdministrativeArea subAdministrativeArea) {
        this.subAdministrativeArea = subAdministrativeArea;
    }

}
