package com.vlad.maskaikin;


import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class Weather extends RealmObject {
    private long id;
    private long time;
    private int temp;
    private byte[] icon;


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
