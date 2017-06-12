package com.sw.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xifanoo on 2017/6/9.
 */

public class Now {
    @SerializedName("tmp")
    private String temperature;
    @SerializedName("cond")
    private More more;

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public More getMore() {
        return more;
    }

    public void setMore(More more) {
        this.more = more;
    }

    public static class More{
        @SerializedName("txt")
        public String info;

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
    }
}
