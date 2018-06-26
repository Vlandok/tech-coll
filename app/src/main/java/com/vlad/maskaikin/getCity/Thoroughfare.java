
package com.vlad.maskaikin.getCity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Thoroughfare {

    @SerializedName("ThoroughfareName")
    @Expose
    private String thoroughfareName;

    public String getThoroughfareName() {
        return thoroughfareName;
    }

    public void setThoroughfareName(String thoroughfareName) {
        this.thoroughfareName = thoroughfareName;
    }

}
