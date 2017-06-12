package com.sw.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xifanoo on 2017/6/12.
 */

public class Forecast {
    private String date;
    @SerializedName("tmp")
    private Tamperature tamperature;
    @SerializedName("cond")
    private More more;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Tamperature getTamperature() {
        return tamperature;
    }

    public void setTamperature(Tamperature tamperature) {
        this.tamperature = tamperature;
    }

    public More getMore() {
        return more;
    }

    public void setMore(More more) {
        this.more = more;
    }

    public static class Tamperature{
        private String max;
        private String min;

        public String getMax() {
            return max;
        }

        public void setMax(String max) {
            this.max = max;
        }

        public String getMin() {
            return min;
        }

        public void setMin(String min) {
            this.min = min;
        }
    }

    public static class More{
        @SerializedName("txt_d")
        private String info;

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
    }
}
